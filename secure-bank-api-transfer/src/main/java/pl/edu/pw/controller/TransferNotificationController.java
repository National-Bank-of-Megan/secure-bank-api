package pl.edu.pw.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pl.edu.pw.domain.Account;
import pl.edu.pw.service.TransferNotificationService;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

@RestController
@Log4j2
@RequestMapping("/api/transfer/notification")
@RequiredArgsConstructor
public class TransferNotificationController {

    private final TransferNotificationService transferNotificationService;

    @GetMapping(value = "/subscribe", headers = "Accept=*/*", consumes = ALL_VALUE, produces = TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribe(@AuthenticationPrincipal Account account) {

        log.info("Client " + account.getClientId() + " is requesting emitter");

        SseEmitter emitter = transferNotificationService.subscribe(account.getClientId());

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(CONTENT_TYPE, TEXT_EVENT_STREAM_VALUE);
        responseHeaders.set("X-Accel-Buffering", "no");

        return new ResponseEntity<>(emitter, responseHeaders, HttpStatus.OK);
    }
}
