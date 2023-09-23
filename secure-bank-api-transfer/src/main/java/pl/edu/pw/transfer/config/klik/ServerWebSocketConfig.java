package pl.edu.pw.transfer.config.klik;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import pl.edu.pw.transfer.service.ServerWebSocketHandler;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class ServerWebSocketConfig implements WebSocketConfigurer, WebSocketMessageBrokerConfigurer {

    private final ServerWebSocketHandler serverWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(serverWebSocketHandler, "/payment/finalize");
    }

//    @Override
//    public void configureMessageBroker(MessageBrokerRegistry config) {
//        config.enableSimpleBroker("/payment/finalize");
//    }
}