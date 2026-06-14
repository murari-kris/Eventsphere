// FIXED: Adjusted the package definition declaration to match your project folder tree structure
package com.college.events.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Broadcast destination prefix pathing endpoints (Where clients subscribe to listen)
        config.enableSimpleBroker("/topic");
        
        // Application filtering destination prefix pathing (Where clients send data)
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Handshake connections mapping configurations setup with clear wildcard CORS allowances
        registry.addEndpoint("/ws-leaderboard")
                .setAllowedOriginPatterns("*") // FIXED: setAllowedOriginPatterns is safer than setAllowedOrigins("*") in newer Spring versions
                .withSockJS(); // Optional addition: provides a reliable fallback configuration option if websockets aren't natively supported by client browsers
    }
}