package com.factoreal.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // STOMP 기반 websocket 메시징 활성화.
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // 메시지 브로커 관련 설정 구성
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 클라이언트가 구독할 수 있는 prefix 경로 활성화.
        // 예) 클라이언트가 "/topic/알림" 으로 구독하면 해당 경로로 메세지를 받을 수 있음.
        registry.enableSimpleBroker("/topic");
        // 클라이언트가 서버로 메세지를 보낼 때 사용할 prefix 경로
        // 예) 클라이언트가 "/app/send" 로 메세지를 보내면, 서버에서는 @MessageMapping("/send") 으로 처리할 수 있습니다.
//        registry.setApplicationDestinationPrefixes("/app"); // 클 -> 서버로 받을 기능은 아직 없어서 주석처리.
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 테스트 용도로 https://jiangxy.github.io.
        registry.addEndpoint("/websocket")
                .setAllowedOrigins("https://jiangxy.github.io","http://localhost:5173")
                .withSockJS();

    }
}
