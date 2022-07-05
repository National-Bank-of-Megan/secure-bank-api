package pl.edu.pw.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.pw.dto.JwtAuthenticationResponse;
import pl.edu.pw.dto.VerifyCodeRequest;
import pl.edu.pw.service.account.AccountService;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/verify")
    public ResponseEntity<?> verifyCode(@Valid @RequestBody VerifyCodeRequest request) {
        Map<String,String> tokens = accountService.verify(request);
        return ResponseEntity.ok(new JwtAuthenticationResponse(tokens.get("access_token"),tokens.get("refresh_token")));
    }


    @GetMapping("/hello")
    public String hello(){
        return "Hello fully authenticated user";
    }



}
