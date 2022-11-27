package pl.edu.pw.service;

import io.github.jav.exposerversdk.PushClientException;
import pl.edu.pw.dto.KlikCodeResponse;

public interface KlikService {
    KlikCodeResponse handleKlikCode(String clientId);
    void sendKlikPushNotification(String accountId) throws PushClientException;
}
