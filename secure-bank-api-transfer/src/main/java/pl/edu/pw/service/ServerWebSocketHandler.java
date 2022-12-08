package pl.edu.pw.service;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import pl.edu.pw.config.klik.PaymentDataWrapper;
import pl.edu.pw.config.klik.WebSocketPool;
import pl.edu.pw.domain.Klik;
import pl.edu.pw.dto.PaymentRequest;
import pl.edu.pw.repository.KlikRepository;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

public class ServerWebSocketHandler extends TextWebSocketHandler {
    @Autowired
    private KlikRepository klikRepository;

    private static final Logger log = LoggerFactory.getLogger(ServerWebSocketHandler.class);

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.info("TRANSPORT ERROR");
        super.handleTransportError(session, exception);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("CONNECTION ESTABLISHED");
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info("GETTING PAYMENT INFORMATION");
        JSONObject payload = new JSONObject(message.getPayload());
        PaymentRequest payment = PaymentRequest.builder()
                .title((String) payload.get("title"))
                .currency((String) payload.get("currency"))
                .amount((BigDecimal) payload.get("amount"))
                .receiverAccountNumber((String) payload.get("receiverAccountNumber"))
                .code((String) payload.get("code")).build();

        log.info(payment.toString());
//        TODO later on delete from the lists
        WebSocketPool.payments.put(
                payment.getCode(),
                PaymentDataWrapper.builder()
                        .webSocketSession(session)
                        .paymentRequest(payment)
                        .build()
        );

        Klik klik = klikRepository.findKlikByKlikCode(payment.getCode()).orElseThrow(
                () -> new IllegalArgumentException("Klik code not found")
        );

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("CONNECTION CLOSING");
//        TODO delete data from WebsocketPool
        super.afterConnectionClosed(session, status);
    }


}
