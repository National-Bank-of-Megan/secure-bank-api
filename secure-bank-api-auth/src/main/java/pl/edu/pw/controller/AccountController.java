package pl.edu.pw.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import pl.edu.pw.dto.AccountRegistration;
import pl.edu.pw.dto.JwtAuthenticationResponse;
import pl.edu.pw.dto.LoginRequest;
import pl.edu.pw.dto.VerifyCodeRequest;
import pl.edu.pw.service.AccountService;
import pl.edu.pw.user.Account;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/api/account")
@AllArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/verify")
    public ResponseEntity<?> verifyCode(@Valid @RequestBody VerifyCodeRequest request) {
        String token = accountService.verify(request);
        return ResponseEntity.ok(new JwtAuthenticationResponse(token, StringUtils.isEmpty(token)));
    }



}
