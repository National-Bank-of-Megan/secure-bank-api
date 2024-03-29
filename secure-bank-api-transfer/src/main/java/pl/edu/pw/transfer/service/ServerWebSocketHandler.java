package pl.edu.pw.transfer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import pl.edu.pw.transfer.config.klik.PaymentDataWrapper;
import pl.edu.pw.transfer.config.klik.WebSocketPool;
import pl.edu.pw.core.domain.Account;
import pl.edu.pw.core.domain.Currency;
import pl.edu.pw.core.domain.Klik;
import pl.edu.pw.transfer.dto.KlikTransferPushNotificationDto;
import pl.edu.pw.transfer.dto.PaymentRequest;
import pl.edu.pw.auth.exception.ResourceNotFoundException;
import pl.edu.pw.auth.repository.AccountRepository;
import pl.edu.pw.core.repository.KlikRepository;
import pl.edu.pw.auth.util.TransferUtil;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Log4j2
@Transactional
public class ServerWebSocketHandler extends TextWebSocketHandler {

    private final AccountRepository accountRepository;
    private final KlikRepository klikRepository;
    private final KlikService klikService;

    private static String invalidKlikCodeMessage(String klikCode) {
        return "Klik code " + klikCode + " has expired or does not exist";
    }

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
                .code((String) payload.get("code"))
                .dateCreated(LocalDateTime.now()).build();

        PaymentDataWrapper currentKlikPayment = WebSocketPool.payments.get(payment.getCode());
        if (Objects.nonNull(currentKlikPayment) && currentKlikPayment.getWebSocketSession().isOpen()) {
            throw new IllegalArgumentException("There is already pending transaction for this klik code");
        }

        log.info(payment.toString());

        String klikCode = payment.getCode();

        Klik klik = klikRepository.findByKlikCode(klikCode)
                .orElseThrow(() -> new RuntimeException(invalidKlikCodeMessage(klikCode)));

        if (!klik.isValid()) {
            throw new RuntimeException(invalidKlikCodeMessage(klikCode));
        }

        Optional<Account> moneyReceiverAccount = accountRepository.findByAccountNumber(payment.getReceiverAccountNumber());
        if (moneyReceiverAccount.isEmpty()) {
            throw new ResourceNotFoundException("Account number " + payment.getReceiverAccountNumber() + " does not exist");
        }

        Account moneySenderAccount = accountRepository.findById(klik.getClientId()).orElseThrow(() ->
                new ResourceNotFoundException("Payment request sender account was not found"));
        if (Objects.isNull(moneySenderAccount.getExpoPushToken())) {
            throw new RuntimeException("Klik transfer is not possible - client hasn't registered his mobile device");
        }

        Currency currency = TransferUtil.getCurrencyFromString(payment.getCurrency());
        BigDecimal currencyBalance = moneySenderAccount.getSubAccounts().get(currency).getBalance();
        if (currencyBalance.subtract(payment.getAmount()).compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Too low " + payment.getCurrency() + " balance on your account");
        }

//        TODO later on delete from the lists
        WebSocketPool.payments.put(
                klikCode,
                PaymentDataWrapper.builder()
                        .webSocketSession(session)
                        .paymentRequest(payment)
                        .build()
        );

        klikService.sendKlikPushNotification(klik.getClientId(), mapToKlikTransferPushNotification(payment));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("CONNECTION CLOSING");
//        TODO delete data from WebsocketPool
        session.close();

        super.afterConnectionClosed(session, status);
    }

    private KlikTransferPushNotificationDto mapToKlikTransferPushNotification(PaymentRequest paymentRequest) {
        return new KlikTransferPushNotificationDto(
                paymentRequest.getTitle(),
                paymentRequest.getReceiverAccountNumber(),
                paymentRequest.getAmount(),
                paymentRequest.getCurrency(),
                paymentRequest.getDateCreated()
        );
    }

}
