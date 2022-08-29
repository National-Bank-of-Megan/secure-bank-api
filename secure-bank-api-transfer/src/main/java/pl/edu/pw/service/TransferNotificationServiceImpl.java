package pl.edu.pw.service;

import lombok.Getter;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pl.edu.pw.domain.AccountDetails;
import pl.edu.pw.domain.Transfer;
import pl.edu.pw.util.CurrentUserUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@Getter
public class TransferNotificationServiceImpl implements TransferNotificationService {

    private Map<String, SseEmitter> subscriptions = new HashMap<>();

    @Override
    public SseEmitter subscribe(String clientId) {
        SseEmitter subscription = new SseEmitter(Long.MAX_VALUE);
        try {
            subscription.send(SseEmitter.event().name("INIT"));
        } catch (IOException e) {
//            todo handle exception
            e.printStackTrace();
        }
        subscriptions.put(clientId, subscription);
        subscription.onCompletion(() -> subscriptions.remove(subscription));
        subscription.onError((e) -> subscriptions.remove(subscription));
        subscription.onTimeout(() -> subscriptions.remove(subscription));
        return subscription;
    }

    @Override
    public void sendNotificationToClient(String receiverClientId, Transfer transfer) {

        AccountDetails sender = transfer.getSender().getAccountDetails();
        String data = new JSONObject()
                .put("title", transfer.getTitle())
                .put("senderFirstname", sender.getFirstName())
                .put("senderLastname", sender.getLastName())
                .put("amount", transfer.getAmount())
                .put("currency", transfer.getCurrency())
                .put("arrivalDate", transfer.getDoneDate()).toString();

        SseEmitter subscription = subscriptions.get(receiverClientId);
        if (subscription != null) {
            try{
                subscription.send(SseEmitter.event().name("TRANSFER_NOTIFICATION").data(data));
            }catch(IOException e){
                e.printStackTrace();
                subscriptions.remove(subscription);
            }
        }


    }
}
