package pl.edu.pw.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jav.exposerversdk.ExpoPushMessage;
import io.github.jav.exposerversdk.PushClient;
import io.github.jav.exposerversdk.PushClientException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.pw.auth.logic.DataGenerator;
import pl.edu.pw.domain.Klik;
import pl.edu.pw.dto.KlikCodeResponse;
import pl.edu.pw.dto.KlikTransferPushNotificationDto;
import pl.edu.pw.repository.KlikRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static pl.edu.pw.constant.Constants.KLIK_CODE_LENGTH;
import static pl.edu.pw.constant.Constants.KLIK_DURATION_SECONDS;

@Service
@Transactional
@RequiredArgsConstructor
public class KlikServiceImpl implements KlikService {

    private final KlikRepository klikRepository;
    private final ObjectMapper objectMapper;

    @Override
    public KlikCodeResponse handleKlikCode(String clientId) {
        Klik klik = klikRepository.findById(clientId).orElseThrow();
        if (!klikCodeValid(klik)) {
            generateNewKlikCode(klik);
        }
        return mapToDto(klik);
    }

    @Override
    public void sendKlikPushNotification(String accountId, KlikTransferPushNotificationDto klikTransferDto) throws PushClientException {

        String recipient = "ExponentPushToken[xyaJ55JedriZTglPBB0g_K]";

        if (!PushClient.isExponentPushToken(recipient)) {
            throw new Error("Token: " + recipient + " is not a valid token.");
        }

        ExpoPushMessage expoPushMessage = createExpoPushMessage(recipient, klikTransferDto);

        List<ExpoPushMessage> expoPushMessages = new ArrayList<>();
        expoPushMessages.add(expoPushMessage);

        sendExpoPushNotificationToRecipients(expoPushMessages);
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

    private void sendExpoPushNotificationToRecipients(List<ExpoPushMessage> expoPushMessages) throws PushClientException {
        PushClient client = new PushClient();
        List<List<ExpoPushMessage>> chunks = client.chunkPushNotifications(expoPushMessages);

        for (List<ExpoPushMessage> chunk : chunks) {
            client.sendPushNotificationsAsync(chunk);
        }
    }

    private void generateNewKlikCode(Klik clientKlik) {
        Set<String> validKlikCodes = klikRepository.findAll().stream().filter(this::klikCodeValid).map(Klik::getKlikCode).collect(Collectors.toSet());
        String newKlikCode = DataGenerator.generateUniqueNumber(validKlikCodes, KLIK_CODE_LENGTH);
        clientKlik.setKlikCode(newKlikCode);
        clientKlik.setGenerateDate(LocalDateTime.now());
        klikRepository.save(clientKlik);
    }

    private boolean klikCodeValid(Klik klik) {
        if (Objects.isNull(klik.getKlikCode()) || Objects.isNull(klik.getGenerateDate())) {
            return false;
        }
        return ChronoUnit.SECONDS.between(klik.getGenerateDate(), LocalDateTime.now()) < KLIK_DURATION_SECONDS;
    }

    private KlikCodeResponse mapToDto(Klik klik) {
        return new KlikCodeResponse(klik.getKlikCode(), klik.getGenerateDate());
    }
}
