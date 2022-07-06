package pl.edu.pw.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.pw.dto.AccountRegistration;
import pl.edu.pw.dto.JwtAuthenticationResponse;
import pl.edu.pw.dto.VerifyCodeRequest;
import pl.edu.pw.service.account.AccountService;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/web")
@RequiredArgsConstructor
public class WebAuthController {

    private static final Logger log = LoggerFactory.getLogger(WebAuthController.class);
    private final AccountService accountService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AccountRegistration registration) {
        accountService.registerAccount(registration);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/login/combination")
    public String getLoginCombination(@Valid @RequestParam String clientId) {
        return accountService.getLoginCombination(clientId);
    }

    @PostMapping("login//verify")
    public ResponseEntity<?> verifyCode(@Valid @RequestBody VerifyCodeRequest request) {
        Map<String,String> tokens = accountService.verify(request);
        return ResponseEntity.ok(new JwtAuthenticationResponse(tokens.get("access_token"),tokens.get("refresh_token")));
    }






}
