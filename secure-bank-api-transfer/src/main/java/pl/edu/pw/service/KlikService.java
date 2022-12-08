package pl.edu.pw.service;

import io.github.jav.exposerversdk.PushClientException;
import pl.edu.pw.dto.KlikCodeResponse;
import pl.edu.pw.dto.KlikTransferPushNotificationDto;

import java.io.IOException;

public interface KlikService {
    KlikCodeResponse handleKlikCode(String clientId);
    void sendKlikPushNotification(String accountId, KlikTransferPushNotificationDto klikTransferDto) throws PushClientException;

    void finalizeKlikTransfer(String senderClientId) throws IOException;
}
