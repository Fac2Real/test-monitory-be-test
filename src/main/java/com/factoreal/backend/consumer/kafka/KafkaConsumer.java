package com.factoreal.backend.consumer.kafka;

import com.factoreal.backend.dto.LogType;
import com.factoreal.backend.dto.SensorKafkaDto;
import com.factoreal.backend.entity.AbnormalLog;
import com.factoreal.backend.sender.WebSocketSender;
import com.factoreal.backend.service.AbnormalLogService;
import com.factoreal.backend.strategy.NotificationStrategy;
import com.factoreal.backend.strategy.NotificationStrategyFactory;
import com.factoreal.backend.strategy.RiskMessageProvider;
import com.factoreal.backend.strategy.enums.AlarmEventDto;
import com.factoreal.backend.strategy.enums.RiskLevel;
import com.factoreal.backend.strategy.enums.SensorType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {

    private final ObjectMapper objectMapper;
    private final WebSocketSender webSocketSender;

    // 알람 푸시 용
    private final NotificationStrategyFactory factory;
    private final RiskMessageProvider messageProvider;

    // 로그 기록용
    private final AbnormalLogService abnormalLogService;

    @KafkaListener(topics = {"EQUIPMENT", "ENVIRONMENT"}, groupId = "${spring.kafka.consumer.group-id:danger-alert-group}")
    public void consume(String message) {
        try {
            SensorKafkaDto dto = objectMapper.readValue(message, SensorKafkaDto.class);

            // equipId가 비어있고 zoneId는 존재할 때만 처리
            if ((dto.getEquipId()!=null) && (Objects.equals(dto.getEquipId(), dto.getZoneId()))) {
                log.info("✅ 수신한 Kafka 메시지: " + message);

                log.info("▶︎ 위험도 감지 start");
                int dangerLevel = getDangerLevel(dto.getSensorType(), dto.getVal());
                log.info("⚠️ 위험도 {} 센서 타입 : {} 감지됨. Zone: {}", dangerLevel, dto.getSensorType(), dto.getZoneId());
                // #################################
                // Abnormal 로그 기록 로직
                // #################################
                SensorType sensorType = SensorType.getSensorType(dto.getSensorType());
                RiskLevel riskLevel = RiskLevel.fromPriority(dangerLevel);
                if (sensorType == null) {
                    log.error("SensorType not found");
                    throw new Exception("SensorType not found");
                }
                AbnormalLog abnormalLog = abnormalLogService.saveAbnormalLogFromKafkaDto(
                        dto,
                        sensorType,
                        riskLevel,
                        LogType.Sensor
                );

                // #################################
                // 웹 앱 SMS 알람 로직
                // #################################
                startAlarm(dto,abnormalLog, riskLevel);

                // #################################
                // 대시보드용 히트맵 로직
                // #################################
                // ❗dangerLevel이 0일 때도 전송해야되면 if 문은 필요없을 것 같아 제거.
                webSocketSender.sendDangerLevel(dto.getZoneId(), dto.getSensorType(), dangerLevel);
            }



        } catch (Exception e) {
            log.error("❌ Kafka 메시지 파싱 실패: {}", message, e);
        }


    }
    @Async
    public void startAlarm(SensorKafkaDto sensorData,AbnormalLog abnormalLog, RiskLevel riskLevel) {
        AlarmEventDto alarmEventDto;
        try{
            // 1. dangerLevel기준으로 alarmEvent 객체 생성.
            alarmEventDto = generateAlarmDto(sensorData, abnormalLog, riskLevel);
        }catch (Exception e){
            log.error("Error converting Kafka message: {}", e);
            return;
        }
        // 1-1. AbnormalLog 기록.
        try {
            // 2. 생성된 AlarmEvent DTO 객체를 사용하여 알람 처리

            log.info("alarmEvent: {}", alarmEventDto.toString());
            processAlarmEvent(alarmEventDto, riskLevel);

        } catch (Exception e) {
            log.error("Error converting Kafka message: {}", e);
            // TODO: 기타 처리 오류 처리
        }
    }
    public static int getDangerLevel(String type, double value) {
        return switch (type) {
            case "temp" -> value > 50 ? 2 : (value > 30 ? 1 : 0);
            case "humid" -> value > 70 ? 2 : (value > 50 ? 1 : 0);
            case "vibration" -> value > 10 ? 2 : (value > 5 ? 1 : 0);
            default -> 0;
        };
    }

    private AlarmEventDto generateAlarmDto(SensorKafkaDto data,AbnormalLog abnormalLog, RiskLevel riskLevel) throws Exception{

        String source = data.getZoneId().equals(data.getEquipId()) ? "공간 센서":"설비 센서";
        SensorType sensorType = SensorType.valueOf(data.getSensorType());

        // 알람 이벤트 객체 반환.
        return AlarmEventDto.builder()
                .eventId(abnormalLog.getId())
                .sensorId(data.getSensorId())
                .equipId(data.getEquipId())
                .zoneId(data.getZoneId())
                .sensorType(sensorType.name())
                .messageBody(abnormalLog.getAbnormalType())
                .source(source)
                .riskLevel(riskLevel)
                .build();
    }

    private void processAlarmEvent(AlarmEventDto alarmEventDto, RiskLevel riskLevel) {
        if (alarmEventDto == null || alarmEventDto.getRiskLevel() == null) {
            log.warn("Received null AlarmEvent DTO or DTO with null severity. Skipping notification.");
            return;
        }

        try {

            if (riskLevel == null) {
                log.warn("Could not map DTO severity '{}' to Entity RiskLevel. Skipping notification.", alarmEventDto.getRiskLevel());
                // TODO: 매핑 실패 시 처리 로직 추가
                return;
            }

            log.info("Processing AlarmEvent with mapped Entity RiskLevel: {}", riskLevel);

            // 3. Factory를 사용하여 매핑된 Entity RiskLevel에 해당하는 NotificationStrategy를 가져와 실행
            List<NotificationStrategy> notificationStrategyList = factory.getStrategiesForLevel(riskLevel);

            log.info("💡Notification strategy executed for AlarmEvent. \n{}",alarmEventDto.toString());
            // 4. 알람 객체의 값으로 전략별 알람 송신.
            notificationStrategyList.forEach(notificationStrategy -> notificationStrategy.send(alarmEventDto));

        } catch (Exception e) {
            log.error("Failed to execute notification strategy for AlarmEvent DTO: {}", alarmEventDto, e);
            // TODO: 전략 실행 중 오류 처리
        }
    }


}
