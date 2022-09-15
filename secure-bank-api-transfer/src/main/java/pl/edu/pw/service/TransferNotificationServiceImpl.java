package pl.edu.pw.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pl.edu.pw.domain.AccountDetails;
import pl.edu.pw.domain.Transfer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@Getter
@RequiredArgsConstructor
public class TransferNotificationServiceImpl implements TransferNotificationService {

    private static final Logger log = LoggerFactory.getLogger(TransferServiceImpl.class);
    private final Map<String, SseEmitter> subscriptions = new HashMap<>();

    @Override
    public SseEmitter subscribe(String clientId) {
        SseEmitter subscription = null;
        if (this.subscriptions.get(clientId) != null) subscription = this.subscriptions.get(clientId);
        else subscription = new SseEmitter();

//        try {
//            log.info("Sending init through subscription: "+subscription);
//            subscription.send(SseEmitter.event().name("INIT"));
//        } catch (IOException e) {
////            todo handle exception
//            System.out.println("===================");
//            e.printStackTrace();
//        }
        log.info("Subscription number before adding :"+this.subscriptions.size());
        log.info("Adding subscription " + subscription);
        this.subscriptions.put(clientId, subscription);
        log.info("Current subscription number: " + this.subscriptions.size());
        subscription.onCompletion(() -> subscriptions.remove(clientId));
        subscription.onError((e) -> {
            log.info("inside on error");
            subscriptions.remove(clientId);
        });
        subscription.onTimeout(subscription::complete);
        return subscription;
    }

    @Override
    public void sendNotificationToClient(String receiverClientId, Transfer transfer) {

        AccountDetails sender = transfer.getSender().getAccountDetails();
        log.info("sending notification to client: "+receiverClientId);
        String data = new JSONObject()
                .put("title", transfer.getTitle())
                .put("senderFirstname", sender.getFirstName())
                .put("senderLastname", sender.getLastName())
                .put("amount", transfer.getAmount())
                .put("currency", transfer.getCurrency())
                .put("arrivalDate", transfer.getDoneDate()).toString();

        SseEmitter subscription = this.subscriptions.get(receiverClientId);
        log.info("sending notification");
        log.info(String.valueOf(subscription));
        log.info("is null? "+(subscription!=null));
        if (subscription != null) {
            try {
                log.info("trying to send notification");
                subscription.send(SseEmitter.event().name("TRANSFER_NOTIFICATION").data(data));
                log.info("notification sent");
            } catch (IOException e) {
                log.error("error sending notification");
                e.printStackTrace();
                subscriptions.remove(subscription);
            }
        }
    }
}
