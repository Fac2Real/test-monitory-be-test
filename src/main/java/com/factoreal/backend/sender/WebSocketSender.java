package com.factoreal.backend.sender;

import com.factoreal.backend.dto.ZoneDangerDto;
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
    public void sendDangerLevel(String zoneId, String sensorType, int level) {
        ZoneDangerDto dangerDto = new ZoneDangerDto(zoneId,sensorType, level);
        messagingTemplate.convertAndSend("/topic/zone", dangerDto);
    }
}
