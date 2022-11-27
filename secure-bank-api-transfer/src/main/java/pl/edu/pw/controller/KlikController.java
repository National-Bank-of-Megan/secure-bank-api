package pl.edu.pw.controller;

import io.github.jav.exposerversdk.PushClientException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pw.domain.Account;
import pl.edu.pw.dto.KlikCodeResponse;
import pl.edu.pw.service.KlikService;

@RestController
@RequestMapping("/api/transfer/klik")
@RequiredArgsConstructor
public class KlikController {

    private final KlikService klikService;

    @GetMapping
    public ResponseEntity<KlikCodeResponse> getKlikCode(@AuthenticationPrincipal Account account) {
        return ResponseEntity.ok(klikService.handleKlikCode(account.getClientId()));
    }

    @PostMapping("/test")
    public ResponseEntity<Void> sendPushNotification(@AuthenticationPrincipal Account account) throws PushClientException {
        klikService.sendKlikPushNotification(account.getClientId());
        return ResponseEntity.ok().build();
    }
}
