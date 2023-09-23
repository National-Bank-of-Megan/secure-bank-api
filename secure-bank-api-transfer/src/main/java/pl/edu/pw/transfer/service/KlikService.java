package pl.edu.pw.transfer.service;

import io.github.jav.exposerversdk.PushClientException;
import pl.edu.pw.transfer.dto.KlikCodeResponse;
import pl.edu.pw.transfer.dto.KlikTransferPushNotificationDto;

import java.io.IOException;

public interface KlikService {
    KlikCodeResponse handleKlikCode(String clientId);

    void sendKlikPushNotification(String receiverClientId, KlikTransferPushNotificationDto klikTransferDto) throws PushClientException;

    void finalizeKlikTransfer(String senderClientId) throws IOException;

    void generateNewKlikCode(String clientId);
}
