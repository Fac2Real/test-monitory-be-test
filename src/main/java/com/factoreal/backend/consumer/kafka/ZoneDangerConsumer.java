package com.factoreal.backend.consumer.kafka;

import com.factoreal.backend.dto.SensorKafkaDto;
import com.factoreal.backend.sender.WebSocketSender;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ZoneDangerConsumer {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final WebSocketSender webSocketSender;


    @KafkaListener(topics = {"EQUIPMENT", "ENVIRONMENT"}, groupId = "monitory-consumer-group")
    public void consume(String message) {

        log.info("✅ 수신한 Kafka 메시지: " + message);

        try {
            SensorKafkaDto dto = objectMapper.readValue(message, SensorKafkaDto.class);

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

    public static int getDangerLevel(String type, double value) {
        return switch (type) {
            case "temp" -> value > 50 ? 2 : (value > 30 ? 1 : 0);
            case "humid" -> value > 70 ? 2 : (value > 50 ? 1 : 0);
            case "vibration" -> value > 10 ? 2 : (value > 5 ? 1 : 0);
            default -> 0;
        };
    }
}
