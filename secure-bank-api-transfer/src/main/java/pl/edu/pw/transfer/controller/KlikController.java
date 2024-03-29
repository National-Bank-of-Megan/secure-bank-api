package pl.edu.pw.transfer.controller;

import io.github.jav.exposerversdk.PushClientException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pw.core.domain.Account;
import pl.edu.pw.transfer.dto.KlikCodeResponse;
import pl.edu.pw.transfer.dto.KlikTransferPushNotificationDto;
import pl.edu.pw.transfer.service.KlikService;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/transfer/klik")
@RequiredArgsConstructor
@PreAuthorize("@accountSecurity.doesUserHaveTransferAuthority()")
public class KlikController {

    private final KlikService klikService;

    @GetMapping
    @PreAuthorize("@accountSecurity.doesUserHaveKlikAuthority()")
    public ResponseEntity<KlikCodeResponse> getKlikCode(@AuthenticationPrincipal Account account) {
        return ResponseEntity.ok(klikService.handleKlikCode(account.getClientId()));
    }

    @PostMapping("/test")
    @PreAuthorize("@accountSecurity.doesUserHaveKlikAuthority()")
    public ResponseEntity<Void> sendPushNotification(@AuthenticationPrincipal Account account) throws PushClientException {
        KlikTransferPushNotificationDto klikTransferDto = new KlikTransferPushNotificationDto(
                "foo", "Frog Shop", BigDecimal.valueOf(99.99), "USD", LocalDateTime.now());
        klikService.sendKlikPushNotification(account.getClientId(), klikTransferDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/payment/confirm")
    @PreAuthorize("@accountSecurity.doesUserHaveKlikAuthority()")
    public ResponseEntity<Void> confirmKlikPayment(@AuthenticationPrincipal Account account) throws IOException {
        klikService.finalizeKlikTransfer(account.getClientId());
        return ResponseEntity.noContent().build();
    }
}
