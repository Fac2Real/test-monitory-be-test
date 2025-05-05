package com.factoreal.backend.consumer.kafka;

import com.factoreal.backend.strategy.NotificationStrategyFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DangerAlertConsumer {
    private final NotificationStrategyFactory factory;
    // TODO 이곳에서 Kafka 메세지 읽은후, RiskLevel 객체를 생성하여 알람 호출
    // Kafka토픽과 메세지 형식이 정의되지 않아 todos 남김
    @KafkaListener(topics = "topic", groupId = "group_1")
    public void listener(Object data){
        System.out.println(data);
    }
}
