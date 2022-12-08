package pl.edu.pw.config.klik;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.*;
import pl.edu.pw.service.ServerWebSocketHandler;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class ServerWebSocketConfig implements WebSocketConfigurer, WebSocketMessageBrokerConfigurer {

    private final ServerWebSocketHandler serverWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(serverWebSocketHandler, "/payment/finalize");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/payment/finalize");
        config.setApplicationDestinationPrefixes("/nbm/api/");

    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/generate")
                .setAllowedOrigins("*")
                .withSockJS();
    }
}