package com.message.Application.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

//	localhost:8080/ws
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Client connects here
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Prefix for messages from server to clients
        registry.enableSimpleBroker("/topic", "/queue"); // simple broker (in-memory)
        // Prefix for messages from clients to controller
        registry.setApplicationDestinationPrefixes("/app");
    }
}
