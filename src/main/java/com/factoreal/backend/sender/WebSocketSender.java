package com.factoreal.backend.sender;

import com.factoreal.backend.dto.SystemLogDto;
import com.factoreal.backend.dto.ZoneDangerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebSocketSender { // 실제로 프론트에 메시지를 전송하는 클래스

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * zone 별로 위험도 메시지를 WebSocket으로 전송
     */
    public void sendDangerLevel(String zoneId, String sensorType, int level) {
        ZoneDangerDto dangerDto = new ZoneDangerDto(zoneId, sensorType, level);
        messagingTemplate.convertAndSend("/topic/zone", dangerDto);
    }

    /**
     * 시스템 로그 전송
     */
    public void sendSystemLog(SystemLogDto logDto) {
        messagingTemplate.convertAndSend("/topic/system-log", logDto);
    }
}
