package com.devcollab.backend.config;

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
        // This is the URL where the frontend will connect to establish the WebSocket link
        registry.addEndpoint("/ws-kanban")
                .setAllowedOriginPatterns("*"); // Allows connections from your frontend development server
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Prefix for messages sent from frontend to the backend handles (@MessageMapping)
        registry.setApplicationDestinationPrefixes("/app");

        // Prefix for topics the frontend will subscribe to for real-time updates
        registry.enableSimpleBroker("/topic");
    }
}