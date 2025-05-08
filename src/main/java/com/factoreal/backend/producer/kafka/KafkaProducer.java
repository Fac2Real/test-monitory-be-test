package com.factoreal.backend.producer.kafka;

import com.factoreal.backend.strategy.enums.AlarmEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendAlarmEvent(String topic, AlarmEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize AlarmEvent", e);
        }
    }
}
