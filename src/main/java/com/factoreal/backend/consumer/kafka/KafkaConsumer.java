package com.factoreal.backend.consumer.kafka;

import com.factoreal.backend.dto.SensorKafkaDto;
import com.factoreal.backend.sender.WebSocketSender;
import com.factoreal.backend.strategy.NotificationStrategy;
import com.factoreal.backend.strategy.NotificationStrategyFactory;
import com.factoreal.backend.strategy.RiskMessageProvider;
import com.factoreal.backend.strategy.enums.AlarmEvent;
import com.factoreal.backend.strategy.enums.RiskLevel;
import com.factoreal.backend.strategy.enums.SensorType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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

    // ELK
    private final RestHighLevelClient elasticsearchClient; // ELK client

    //    @KafkaListener(topics = {"EQUIPMENT", "ENVIRONMENT"}, groupId = "monitory-consumer-group-1")
    @KafkaListener(topics = {"EQUIPMENT", "ENVIRONMENT"}, groupId = "${spring.kafka.consumer.group-id:danger-alert-group}")
    public void consume(String message) {

        log.info("✅ 수신한 Kafka 메시지: " + message);
        // #################################
        // 대시보드용 히트맵 로직
        // #################################
        try {
            SensorKafkaDto dto = objectMapper.readValue(message, SensorKafkaDto.class);

            // 비동기 알림
            startAlarm(dto);

            // 비동기 ES 저장
            saveToElasticsearch(dto);

            // equipId가 비어있고 zoneId는 존재할 때만 처리
            if ((dto.getEquipId() == null || dto.getEquipId().isEmpty()) && dto.getZoneId() != null) {

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

    // ✅ Elastic 비동기 저장
    @Async
    public void saveToElasticsearch(SensorKafkaDto dto) {
        try {
            Map<String, Object> map = objectMapper.convertValue(dto, new TypeReference<>() {});
            map.put("timestamp", Instant.now().toString());  // 타임필드 추가

            IndexRequest request = new IndexRequest("sensor-data").source(map);
            elasticsearchClient.index(request, RequestOptions.DEFAULT);

            log.info("✅ Elasticsearch 저장 완료: {}", dto.getSensorId());
        } catch (Exception e) {
            log.error("❌ Elasticsearch 저장 실패: {}", dto, e);
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
    public static int getDangerLevel(String type, double value) {
        return switch (type) {
            case "temp" -> value > 50 ? 2 : (value > 30 ? 1 : 0);
            case "humid" -> value > 70 ? 2 : (value > 50 ? 1 : 0);
            case "vibration" -> value > 10 ? 2 : (value > 5 ? 1 : 0);
            default -> 0;
        };
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
