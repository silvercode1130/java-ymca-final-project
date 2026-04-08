package com.springbootstudy.bbs.configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 클라이언트가 실제로 연결하는 엔드포인트
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*"); // 처음엔 편하게 풀어서, 나중에 도메인 제한
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 클라이언트에서 서버로 보낼 때 앞에 붙이는 prefix
        registry.setApplicationDestinationPrefixes("/pub");

        // 서버가 클라이언트에게 보내는(브로커가 처리하는) prefix
        registry.enableSimpleBroker("/topic", "/queue");
    }
}