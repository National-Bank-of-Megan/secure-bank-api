package pl.edu.pw.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pl.edu.pw.domain.Account;
import pl.edu.pw.service.TransferNotificationService;
import pl.edu.pw.util.CurrentUserUtil;

@CrossOrigin
@RestController
@RequestMapping("/api/transfer/notification")
@RequiredArgsConstructor
public class TransferNotificationController {

    private final TransferNotificationService transferNotificationService;

    @GetMapping("/subscribe")
    public ResponseEntity<SseEmitter> subscribe (){
//        Account account = CurrentUserUtil.getCurrentUser();
        return ResponseEntity.ok(transferNotificationService.subscribe("12345679"));
    }
}
