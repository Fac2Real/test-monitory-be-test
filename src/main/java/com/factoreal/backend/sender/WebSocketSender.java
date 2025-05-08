package com.factoreal.backend.sender;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebSocketSender {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * zone 별로 위험도 메시지를 WebSocket으로 전송
     */
    public void sendDangerLevel(String zoneId, int level) {
        messagingTemplate.convertAndSend("/topic/zone/" + zoneId, level);
    }
}
