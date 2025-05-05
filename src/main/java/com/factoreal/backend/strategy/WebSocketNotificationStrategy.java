package com.factoreal.backend.strategy;

import com.factoreal.backend.entity.enums.RiskLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@RequiredArgsConstructor
@Component
public class WebSocketNotificationStrategy implements  NotificationStrategy{
    // SimpMessagingTemplate은 WebSocketConfig.java에 EnableWebSocketMessageBroker 어노테이션에 의해 빈이 등록됨.
    private final SimpMessagingTemplate messageTemplate;

    @Override
    public void send(String userId, String message) {
        // /topic/userId로 메세지를 전송 => userId를 구분하여 웹 알람 발송
        // TODO 대시보드 전체에서 보여져야 하는 로직이면 고정 토픽으로 구분없이 보여주는 것도 좋을 듯
        messageTemplate.convertAndSend("/topic/"+userId, message);
    }

    @Override
    public RiskLevel getSupportedLevel() {
        return RiskLevel.DANGER;
    }
}
