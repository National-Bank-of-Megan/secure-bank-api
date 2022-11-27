package pl.edu.pw.service;

import io.github.jav.exposerversdk.ExpoPushMessage;
import io.github.jav.exposerversdk.ExpoPushTicket;
import io.github.jav.exposerversdk.PushClient;
import io.github.jav.exposerversdk.PushClientException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.pw.auth.logic.DataGenerator;
import pl.edu.pw.domain.Klik;
import pl.edu.pw.dto.KlikCodeResponse;
import pl.edu.pw.repository.KlikRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static pl.edu.pw.constant.Constants.KLIK_CODE_LENGTH;
import static pl.edu.pw.constant.Constants.KLIK_DURATION_SECONDS;

@Service
@Transactional
@RequiredArgsConstructor
public class KlikServiceImpl implements KlikService {

    private final KlikRepository klikRepository;

    @Override
    public KlikCodeResponse handleKlikCode(String clientId) {
        Klik klik = klikRepository.findById(clientId).orElseThrow();
        if (!klikCodeValid(klik)) {
            generateNewKlikCode(klik);
        }
        return mapToDto(klik);
    }

    @Override
    public void sendKlikPushNotification(String accountId) throws PushClientException {
        String recipient = "ExponentPushToken[xyaJ55JedriZTglPBB0g_K]";
        String title = "My message title!";
        String message = "A push message from ExampleExpoServer";

        if (!PushClient.isExponentPushToken(recipient)) {
            throw new Error("Token:" + recipient + " is not a valid token.");
        }

        ExpoPushMessage expoPushMessage = new ExpoPushMessage();
        expoPushMessage.getTo().add(recipient);
        expoPushMessage.setTitle(title);
        expoPushMessage.setBody(message);

        List<ExpoPushMessage> expoPushMessages = new ArrayList<>();
        expoPushMessages.add(expoPushMessage);

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
