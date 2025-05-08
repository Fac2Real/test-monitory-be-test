package com.factoreal.backend.consumer;

import com.factoreal.backend.dto.SensorKafkaDto;
import com.factoreal.backend.sender.WebSocketSender;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaSensorConsumer {

    private final ObjectMapper objectMapper = new ObjectMapper(); // 데이터 파싱
    private final WebSocketSender webSocketSender; // 웹소켓
    private final RestHighLevelClient elasticsearchClient; // ELK client


    @KafkaListener(topics = {"EQUIPMENT", "ENVIRONMENT"}, groupId = "monitory-consumer-group")
    public void consume(String message) {

        log.info("✅ 수신한 Kafka 메시지: " + message);

        try {
            SensorKafkaDto dto = objectMapper.readValue(message, SensorKafkaDto.class);


            // ✅ Elasticsearch 저장
            Map<String, Object> map = objectMapper.convertValue(dto, new TypeReference<>() {});
            map.put("timestamp", Instant.now().toString());  //  현재 UTC 시간 ISO 8601 형식 문자열
            IndexRequest request = new IndexRequest("sensor-data")
                    .source(map);
            elasticsearchClient.index(request, RequestOptions.DEFAULT);

            // ⚠️ 위험 감지 및 웹소켓 전송
            // equipId가 비어있고 zoneId는 존재할 때만 처리
            if ((dto.getEquipId() == null || dto.getEquipId().isEmpty()) && dto.getZoneId() != null) {

                log.info("▶︎ 위험도 감지 start");
                int dangerLevel = getDangerLevel(dto.getSensorType(), dto.getVal());

                if (dangerLevel > 0) {
                    log.info("⚠️ 위험도 {} 감지됨. Zone: {}", dangerLevel, dto.getZoneId());
                    webSocketSender.sendDangerLevel(dto.getZoneId(), dangerLevel);
                }
            }

        } catch (Exception e) {
            log.error("❌ Kafka 메시지 파싱 실패: {}", message, e);
        }
    }

    private int getDangerLevel(String type, double value) {
        return switch (type) {
            case "temp" -> value > 50 ? 2 : (value > 30 ? 1 : 0);
            case "humid" -> value > 70 ? 2 : (value > 50 ? 1 : 0);
            case "vibration" -> value > 10 ? 2 : (value > 5 ? 1 : 0);
            default -> 0;
        };
    }
}
