package com.factoreal.backend.consumer.kafka;

import com.factoreal.backend.dto.SensorKafkaDto;
import com.factoreal.backend.sender.WebSocketSender;
import com.factoreal.backend.strategy.NotificationStrategy;
import com.factoreal.backend.strategy.NotificationStrategyFactory;
import com.factoreal.backend.strategy.RiskMessageProvider;
import com.factoreal.backend.strategy.enums.AlarmEvent;
import com.factoreal.backend.strategy.enums.RiskLevel;
import com.factoreal.backend.strategy.enums.SensorType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {

    private final ObjectMapper objectMapper;
    private final WebSocketSender webSocketSender;

    // 알람 푸시 용
    private final NotificationStrategyFactory factory;
    private final RiskMessageProvider messageProvider;

    @KafkaListener(topics = {"EQUIPMENT", "ENVIRONMENT"}, groupId = "monitory-consumer-group")
    public void consume(String message) {

        log.info("✅ 수신한 Kafka 메시지: " + message);
        // #################################
        // 대시보드용 히트맵 로직
        // #################################
        try {
            SensorKafkaDto dto = objectMapper.readValue(message, SensorKafkaDto.class);
            startAlarm(dto);
            // equipId와 zoneId가 같을 때만 처리 - 공간 센서(not 설비 센서)
            if (dto.getEquipId() != null && dto.getEquipId().equals(dto.getZoneId()) && dto.getZoneId() != null) {

                log.info("▶︎ 위험도 감지 start");
                int dangerLevel = getDangerLevel(dto.getSensorType(), dto.getVal());

                if (dangerLevel > 0) {
                    log.info("⚠️ 위험도 {} 센서 타입 : {} 감지됨. Zone: {}", dangerLevel, dto.getSensorType(), dto.getZoneId());
                    webSocketSender.sendDangerLevel(dto.getZoneId(), dto.getSensorType(), dangerLevel);
                }
            }



        } catch (Exception e) {
            log.error("❌ Kafka 메시지 파싱 실패: {}", message, e);
        }


    }
    
    @Async
    public void startAlarm(SensorKafkaDto sensorData){
        AlarmEvent alarmEvent;
        try{
            alarmEvent = generateAlarmDto(sensorData);
        }catch (Exception e){
            log.error("Error converting Kafka message: {}", e);
            return;
        }
        try {
            // 2. 생성된 AlarmEvent DTO 객체를 사용하여 알람 처리

            log.info("alarmEvent: {}",alarmEvent.toString());
            processAlarmEvent(alarmEvent);

        } catch (Exception e) {
            log.error("Error converting Kafka message: {}", e);
            // TODO: 기타 처리 오류 처리
        }
    }

    private static int getDangerLevel(String sensorType, double value) { // 위험도 계산 메서드
        return switch (sensorType) { // 센서 타입에 따른 위험도 계산
            case "temp" -> { // 온도 위험도 기준 (KOSHA: https://www.kosha.or.kr/)
                if (value > 40 || value < -35)        // >40℃ 또는 < -35℃ → 위험 (작업 중단 권고)
                    yield 2;
                else if (value > 30 || value < 25)   // >30℃ 또는 < 25℃ → 주의 (작업 제한 또는 휴식 권고)
                    yield 1;
                else                                 // 25℃ ≤ value ≤ 30℃ → 안전 (권장 18~21℃)
                    yield 0;
            }
            
            case "humid" -> { // 상대습도 위험도 기준 (OSHA, ACGIH TLV®, NIOSH)
                if (value >= 80)             // RH ≥ 80% → 위험
                    yield 2;
                else if (value >= 60)        // 60% ≤ RH < 80% → 주의
                    yield 1;
                else                         // RH < 60% → 안전
                    yield 0;
            }
            
            case "vibration" -> { // 진동 위험도 기준 (ISO 10816-3)
                if (value > 7.1)            // >7.1 mm/s → 위험 (2)
                    yield 2;
                else if (value > 2.8)       // >2.8 mm/s → 주의 (1)
                    yield 1;
                else                        // ≤2.8 mm/s → 안전 (0)
                    yield 0;
            }

            case "current" -> { // 전류 위험도 기준 (KEPCO)
                if (value >= 30)        // ≥30 mA → 위험 (강한 경련, 심실세동 및 사망 위험)
                    yield 2;
                else if (value >= 7)    // ≥7 mA → 주의 (고통 한계 전류, 불수전류)
                    yield 1;
                else                    // <7 mA → 안전 (감지전류 수준)
                    yield 0;
            }

            case "dust" -> { // PM2.5 위험도 기준 (고용노동부)
                if (value >= 150)              // ≥ 150㎍/㎥ → 위험 (2)
                    yield 2;
                else if (value >= 75)          // ≥ 75㎍/㎥ → 주의 (1)
                    yield 1;
                else                            // < 75㎍/㎥ → 안전 (0)
                    yield 0;
            }
            
            // 그 외 센서 타입은 안전
            default -> 0;
        }; // switch 끝
    }

    private AlarmEvent generateAlarmDto(SensorKafkaDto data) throws Exception{
        Stream<SensorType> sensorTypes = Stream.of(SensorType.values());

        SensorType sensorType = sensorTypes.filter(
                s -> s.name().equals(data.getSensorType())
        ).findFirst().orElse(null);

        if (sensorType == null) {
            throw new Exception("SensorType not found");
        }

        int dangerLevel = KafkaConsumer.getDangerLevel(sensorType.name(),data.getVal());
        RiskLevel riskLevel = RiskLevel.fromPriority(dangerLevel);
        String source = data.getZoneId().equals(data.getEquipId()) ? "공간 센서":"설비 센서";

        // 위험 레벨 별 알람 객체 생성.
        String messageBody = messageProvider.getMessage(sensorType,riskLevel);


        // 알람 이벤트 객체 반환.
        return AlarmEvent.builder()
                .eventId(UUID.randomUUID())
                .sensorType(String.valueOf(sensorType))
                .sensorValue(data.getVal())
                .messageBody(messageProvider.getMessage(sensorType,riskLevel))
                .source(source)
                .riskLevel(riskLevel)
                .timestamp(Timestamp.valueOf(LocalDateTime.now()))
                .build();
    }

    private void processAlarmEvent(AlarmEvent alarmEventDto) {
        if (alarmEventDto == null || alarmEventDto.riskLevel() == null) {
            log.warn("Received null AlarmEvent DTO or DTO with null severity. Skipping notification.");
            return;
        }

        try {
            // DTO의 severity (AlarmEvent.RiskLevel)를 Entity RiskLevel로 매핑
            RiskLevel entityRiskLevel = mapDtoSeverityToEntityRiskLevel(alarmEventDto.riskLevel());

            if (entityRiskLevel == null) {
                log.warn("Could not map DTO severity '{}' to Entity RiskLevel. Skipping notification.", alarmEventDto.riskLevel());
                // TODO: 매핑 실패 시 처리 로직 추가
                return;
            }

            log.info("Processing AlarmEvent with mapped Entity RiskLevel: {}", entityRiskLevel);

            // 3. Factory를 사용하여 매핑된 Entity RiskLevel에 해당하는 NotificationStrategy를 가져와 실행
            List<NotificationStrategy> notificationStrategyList = factory.getStrategiesForLevel(entityRiskLevel);

            log.info("💡Notification strategy executed for AlarmEvent. \n{}",alarmEventDto.toString());
            // 4. 알람 객체의 값으로 전략별 알람 송신.
            notificationStrategyList.forEach(notificationStrategy -> notificationStrategy.send(alarmEventDto));

        } catch (Exception e) {
            log.error("Failed to execute notification strategy for AlarmEvent DTO: {}", alarmEventDto, e);
            // TODO: 전략 실행 중 오류 처리
        }
    }

    /**
     * DTO의 AlarmEvent.RiskLevel(Kafka)을 Entity의 RiskLevel로 매핑합니다.
     * Factory에서는 Entity의 RiskLevel을 사용해야 합니다.
     */
    private RiskLevel mapDtoSeverityToEntityRiskLevel(RiskLevel dtoSeverity) {
        if (dtoSeverity == null) {
            return null;
        }
        // DTO의 심각도 수준에 따라 Entity RiskLevel 매핑
        // CRITICAL -> DANGER (높은 위험)
        // WARNING, INFO -> WARN (낮은 위험/정보)
        return switch (dtoSeverity) {
            case CRITICAL -> RiskLevel.CRITICAL;
            case WARNING, INFO -> RiskLevel.WARNING;
            default -> {
                log.warn("Unknown AlarmEvent DTO severity received: {}. Mapping to WARN.", dtoSeverity);
                yield RiskLevel.WARNING; // 알 수 없는 값은 기본 WARN으로 처리
            }
        };
    }
}
