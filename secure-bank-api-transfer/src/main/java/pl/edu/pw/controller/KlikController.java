package pl.edu.pw.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.TextMessage;
import pl.edu.pw.config.klik.WebSocketPool;
import pl.edu.pw.domain.Account;
import pl.edu.pw.dto.KlikCodeResponse;
import pl.edu.pw.service.KlikService;

import java.io.IOException;

@RestController
@RequestMapping("/api/transfer/klik")
@RequiredArgsConstructor
public class KlikController {

    private final KlikService klikService;

    @GetMapping
    public ResponseEntity<KlikCodeResponse> getKlikCode(@AuthenticationPrincipal Account account) {
        return ResponseEntity.ok(klikService.handleKlikCode(account.getClientId()));
    }

    @GetMapping("/payment/confirm")
    public void confirmKlikPayment(){
        WebSocketPool.payments.forEach((key,entry) ->{
            try {
                entry.getWebSocketSession().sendMessage(
                        new TextMessage("Payment accepted")
                );
                WebSocketPool.payments.remove(key);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
