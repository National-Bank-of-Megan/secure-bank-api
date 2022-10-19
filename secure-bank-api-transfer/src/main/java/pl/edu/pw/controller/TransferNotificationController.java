package pl.edu.pw.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pl.edu.pw.domain.Account;
import pl.edu.pw.service.TransferNotificationService;

//@CrossOrigin
@RestController
@RequestMapping("/api/transfer/notification")
@RequiredArgsConstructor
public class TransferNotificationController {

    private final TransferNotificationService transferNotificationService;
    private static final Logger log = LoggerFactory.getLogger(TransferNotificationController.class);

    @GetMapping(value = "/subscribe", consumes = MediaType.ALL_VALUE, produces = {MediaType.TEXT_EVENT_STREAM_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<SseEmitter> subscribe(@AuthenticationPrincipal Account account) {
        log.info("client " + account.getClientId() + " is requesting emitter");
        SseEmitter emitter = transferNotificationService.subscribe(account.getClientId());
        return ResponseEntity.ok(emitter);
    }
}
