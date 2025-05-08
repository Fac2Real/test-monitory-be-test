package com.factoreal.backend.consumer.kafka;

import com.factoreal.backend.dto.SensorDataDto;
import com.factoreal.backend.strategy.NotificationStrategy;
import com.factoreal.backend.strategy.NotificationStrategyFactory;
import com.factoreal.backend.strategy.RiskMessageProvider;
import com.factoreal.backend.strategy.enums.AlarmEvent;
import com.factoreal.backend.strategy.enums.RiskLevel;
import com.factoreal.backend.strategy.enums.SensorType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class DangerAlertConsumer {

    private final NotificationStrategyFactory factory;
    private final ObjectMapper objectMapper; // Inject ObjectMapper
    private final RiskMessageProvider messageProvider;

    // 작업장 환경 토픽 구독
    @KafkaListener(
            topics = {"EQUIPMENT", "ENVIRONMENT"},
            groupId = "${spring.kafka.consumer.group-id:monitory-consumer-group}"
    )
    public void listenForDangerAlerts(String message) {
        log.info("Received Kafka message: {}", message);
        AlarmEvent alarmEvent;
        try {
            // Kafka에서 받아온 객체
            SensorDataDto sensorData = objectMapper.readValue(message, SensorDataDto.class);

            alarmEvent = generateAlarmDto(sensorData);

        } catch (JsonProcessingException e) {
            log.error("Failed to parse Kafka message as AlarmEvent DTO: {}", message, e);
            // TODO: 파싱 오류 처리 (예: 로그 기록 후 무시, Dead-Letter Queue로 전송 등)
            return;
        }catch (Exception e){
            log.error("Error converting Kafka message: {}", e);
            return;
        }

        try {
            // 2. 생성된 AlarmEvent DTO 객체를 사용하여 알람 처리

            log.info("alarmEvent: {}",alarmEvent.toString());
            processAlarmEvent(alarmEvent);

        } catch (Exception e) {
            log.error("An error occurred while processing AlarmEvent: {}", message, e);
            // TODO: 기타 처리 오류 처리
        }
    }

    private AlarmEvent generateAlarmDto(SensorDataDto data) throws Exception{
        Stream<SensorType> sensorTypes = Stream.of(SensorType.values());

        SensorType sensorType = sensorTypes.filter(
                s -> s.name().equals(data.getSensorType())
        ).findFirst().orElse(null);

        if (sensorType == null) {
            throw new Exception("SensorType not found");
        }

        int dangerLevel = ZoneDangerConsumer.getDangerLevel(sensorType.name(),data.getVal());
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