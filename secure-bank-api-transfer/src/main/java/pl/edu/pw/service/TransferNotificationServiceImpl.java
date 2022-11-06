package pl.edu.pw.service;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pl.edu.pw.domain.AccountDetails;
import pl.edu.pw.domain.Transfer;
import pl.edu.pw.dto.TransferNotificationDto;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static pl.edu.pw.constant.Constants.NOTIFICATION_EVENT_NAME;

@Service
@Log4j2
public class TransferNotificationServiceImpl implements TransferNotificationService {

    private final Map<String, SseEmitter> subscriptions = new HashMap<>();

    private final long emitterTimeout;

    public TransferNotificationServiceImpl(@Value("${notifications.emitterTimeout}") long emitterTimeout) {
        this.emitterTimeout = emitterTimeout;
    }

    @Override
    public SseEmitter subscribe(String clientId) {

        if (subscriptions.get(clientId) == null) {
            SseEmitter emitter = new SseEmitter(emitterTimeout);

            emitter.onCompletion(() -> {
                log.info("Emitter completed");
                subscriptions.remove(clientId);
            });
            emitter.onTimeout(() -> {
                log.info("Emitter timed out");
                subscriptions.remove(clientId);
            });
            emitter.onError(e ->  {
                log.error("Create SseEmitter exception", e);
                subscriptions.remove(clientId);
            });

            subscriptions.put(clientId, emitter);
        }

        log.info("User with id " + clientId + " subscribed to transfer notifications");

        return subscriptions.get(clientId);
    }

    @Override
    public void sendNotificationToClient(String receiverClientId, Transfer transfer) {

        AccountDetails sender = transfer.getSender().getAccountDetails();

        TransferNotificationDto transferNotificationDto = new TransferNotificationDto(transfer.getTitle(), sender.getFirstName(),
                sender.getLastName(), transfer.getAmount(), transfer.getCurrency().name(), transfer.getDoneDate()
        );

        SseEmitter emitter = subscriptions.get(receiverClientId);

        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .reconnectTime(30_000)
                        .name(NOTIFICATION_EVENT_NAME)
                        .id(receiverClientId)
                        .data(transferNotificationDto)
                );
                log.info("Notification sent to " + receiverClientId);
            } catch (IOException e) {
                log.error("Send notification error: " + e.getCause());
                emitter.completeWithError(e);
            }
        }
    }
}
