package pl.edu.pw.service;

import pl.edu.pw.dto.KlikCodeResponse;

public interface KlikService {
    KlikCodeResponse handleKlikCode(String clientId);
}
