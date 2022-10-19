package pl.edu.pw.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pl.edu.pw.domain.Transfer;

public interface TransferNotificationService {

    SseEmitter subscribe(String clientId);

    void sendNotificationToClient(String receiverClientId, Transfer transfer);
}
