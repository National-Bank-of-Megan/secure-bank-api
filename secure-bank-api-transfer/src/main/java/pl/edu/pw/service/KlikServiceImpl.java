package pl.edu.pw.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jav.exposerversdk.ExpoPushMessage;
import io.github.jav.exposerversdk.PushClient;
import io.github.jav.exposerversdk.PushClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import pl.edu.pw.auth.logic.DataGenerator;
import pl.edu.pw.config.klik.PaymentDataWrapper;
import pl.edu.pw.config.klik.WebSocketPool;
import pl.edu.pw.domain.*;
import pl.edu.pw.domain.Currency;
import pl.edu.pw.dto.KlikCodeResponse;
import pl.edu.pw.dto.KlikTransferPushNotificationDto;
import pl.edu.pw.dto.PaymentRequest;
import pl.edu.pw.dto.TransferCreate;
import pl.edu.pw.exception.ResourceNotFoundException;
import pl.edu.pw.repository.AccountRepository;
import pl.edu.pw.repository.KlikRepository;
import pl.edu.pw.repository.TransferRepository;

import javax.transaction.Transactional;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.SECONDS;
import static pl.edu.pw.constant.Constants.*;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class KlikServiceImpl implements KlikService {

    private final KlikRepository klikRepository;
    private final TransferRepository transferRepository;
    private final AccountRepository accountRepository;
    private final TransferNotificationService transferNotificationService;

    private final ObjectMapper objectMapper;

    @Override
    public KlikCodeResponse handleKlikCode(String clientId) {
        Klik klik = klikRepository.getByClientId(clientId);
        if (!klik.isValid()) {
            generateNewKlikCode(klik);
        }
        return mapToDto(klik);
    }

    @Override
    public void sendKlikPushNotification(String receiverClientId, KlikTransferPushNotificationDto klikTransferDto) throws PushClientException {
        Account paymentRequestReceiverAccount = accountRepository.findById(receiverClientId).orElseThrow(() ->
            new ResourceNotFoundException("Payment request receiver account was not found"));

        String receiverAccountExpoPushToken = paymentRequestReceiverAccount.getExpoPushToken();
        if (Objects.isNull(receiverAccountExpoPushToken)) {
            throw new RuntimeException("Klik transfer is not possible - client hasn't registered his mobile device");
        }

        String completeReceiverAccountExpoPushToken = String.format("ExponentPushToken[%s]", receiverAccountExpoPushToken);

        if (!PushClient.isExponentPushToken(completeReceiverAccountExpoPushToken)) {
            throw new IllegalArgumentException("Token: " + completeReceiverAccountExpoPushToken + " is not a valid token");
        }

        ExpoPushMessage expoPushMessage = createExpoPushMessage(completeReceiverAccountExpoPushToken, klikTransferDto);

        List<ExpoPushMessage> expoPushMessages = new ArrayList<>();
        expoPushMessages.add(expoPushMessage);

        sendExpoPushNotificationToRecipient(expoPushMessages);
    }

    @Override
    public void finalizeKlikTransfer(String senderClientId) throws IOException {
        Klik klik = klikRepository.getByClientId(senderClientId);
        String klikCode = klik.getKlikCode();
        if (Objects.isNull(klikCode) || Objects.isNull(WebSocketPool.payments.get(klikCode))) {
            throw new RuntimeException(NO_ACTIVE_KLIK_PAYMENT);
        }

        PaymentDataWrapper paymentDataWrapper = WebSocketPool.payments.get(klikCode);
        PaymentRequest paymentRequest = paymentDataWrapper.getPaymentRequest();
        if (SECONDS.between(paymentRequest.getDateCreated(), LocalDateTime.now()) > KLIK_CONFIRM_PAYMENT_DURATION_SECONDS) {
            throw new RuntimeException(NO_ACTIVE_KLIK_PAYMENT);
        }

        TransferCreate transferCreate = mapFromPaymentRequestToTransferCreate(paymentRequest);

        Account senderAccount = accountRepository.findById(senderClientId).orElseThrow(() ->
                new ResourceNotFoundException("Account with " + senderClientId + " client id was not found"));
        Account receiverAccount = accountRepository.findByAccountNumber(transferCreate.getReceiverAccountNumber()).orElseThrow(() ->
                new ResourceNotFoundException("Account with " + transferCreate.getReceiverAccountNumber() + " account number was not found"));

        Currency currency = Currency.valueOf(transferCreate.getCurrency());
        BigDecimal amount = transferCreate.getAmount();
        senderAccount.chargeCurrencyBalance(currency, amount);
        receiverAccount.addCurrencyBalance(currency, amount);

        Transfer transferToSave = TransferServiceImpl.TransferMapper.map(transferCreate, senderAccount, receiverAccount);
        transferToSave.setDoneDate(LocalDateTime.now());
        transferToSave.setStatus(Status.DONE);

        Transfer savedTransfer = transferRepository.save(transferToSave);
        // Send klik transfer done notification here

        paymentDataWrapper.getWebSocketSession().sendMessage(new TextMessage("Klik payment accepted"));
        PaymentDataWrapper paymentSessionToClose = WebSocketPool.payments.remove(klikCode);
        paymentSessionToClose.getWebSocketSession().close();
        generateNewKlikCode(klik);
    }

    @Override
    public void generateNewKlikCode(String clientId) {
        Klik klik = klikRepository.getByClientId(clientId);
        generateNewKlikCode(klik);
    }

    private void generateNewKlikCode(Klik clientKlik) {
        Set<String> validKlikCodes = klikRepository.findAll().stream().filter(Klik::isValid).map(Klik::getKlikCode).collect(Collectors.toSet());
        String newKlikCode = DataGenerator.generateUniqueNumber(validKlikCodes, KLIK_CODE_LENGTH);
        clientKlik.setKlikCode(newKlikCode);
        clientKlik.setGenerateDate(LocalDateTime.now());
        klikRepository.save(clientKlik);
    }

    private ExpoPushMessage createExpoPushMessage(String recipient, KlikTransferPushNotificationDto klikTransferDto) {
        final String title = "Confirm Klik payment";
        final String message = "Tap to open Klik confirm payment screen";

        Map<String, Object> klikPaymentData = objectMapper.convertValue(klikTransferDto, new TypeReference<>() {});

        ExpoPushMessage expoPushMessage = new ExpoPushMessage();
        expoPushMessage.getTo().add(recipient);
        expoPushMessage.setTitle(title);
        expoPushMessage.setBody(message);
        expoPushMessage.setData(klikPaymentData);

        return expoPushMessage;
    }

    private void sendExpoPushNotificationToRecipient(List<ExpoPushMessage> expoPushMessages) throws PushClientException {
        PushClient client = new PushClient();
        List<List<ExpoPushMessage>> chunks = client.chunkPushNotifications(expoPushMessages);

        for (List<ExpoPushMessage> chunk : chunks) {
            client.sendPushNotificationsAsync(chunk);
        }
    }

    private KlikCodeResponse mapToDto(Klik klik) {
        return new KlikCodeResponse(klik.getKlikCode(), klik.getGenerateDate());
    }

    private TransferCreate mapFromPaymentRequestToTransferCreate(PaymentRequest paymentRequest) {
        return new TransferCreate(
                paymentRequest.getTitle(),
                paymentRequest.getReceiverAccountNumber(),
                paymentRequest.getAmount(),
                paymentRequest.getCurrency());
    }
}
