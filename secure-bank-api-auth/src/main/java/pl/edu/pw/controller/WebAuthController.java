package pl.edu.pw.controller;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import pl.edu.pw.domain.Account;
import pl.edu.pw.dto.AccountRegistration;
import pl.edu.pw.dto.JwtAuthenticationResponse;
import pl.edu.pw.dto.VerifyCodeRequest;
import pl.edu.pw.repository.AccountRepository;
import pl.edu.pw.service.account.AccountService;
import pl.edu.pw.util.JWTUtil;
import pl.edu.pw.util.http.HttpRequestUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/web")
@RequiredArgsConstructor
public class WebAuthController {

    @Value("${jwt.expirationTime}")
    private long jwtExpirationTime;

    @Value("${refreshToken.expirationTime}")
    private long refreshTokenExpirationTime;

    @Value("${jwt.secret}")
    private String jwtSecret;

    private static final Logger log = LoggerFactory.getLogger(WebAuthController.class);
    private final AccountService accountService;
    private final AccountRepository accountRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AccountRegistration registration, HttpServletRequest request) {
        registration.setLocalIp(getLocalIpAddress());
        registration.setPublicIp(HttpRequestUtils.getClientIpAddressFromRequest(request));
        String qr = accountService.registerAccount(registration);
        return ResponseEntity.created(URI.create("/register")).body(qr);
    }

    private String getLocalIpAddress() {
        String localIp;
        try {
            localIp = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            localIp = "UNKNOWN";
        }
        return localIp;
    }

    @GetMapping("/login/combination")
    public String getLoginCombination(@Valid @RequestParam String clientId) {
        return accountService.getLoginCombination(clientId);
    }

    @PostMapping("/login/verify")
    public ResponseEntity<?> verifyCode(@Valid @RequestBody VerifyCodeRequest request, HttpServletRequest httpRequest, HttpServletResponse response) throws IOException {

        if (accountService.verify(request, httpRequest)) {
            Account account = accountRepository.findByClientId(request.getClientId()).orElse(null);
            Map<String, String> bodyResponse = new HashMap<>();
            bodyResponse.put("access_token", JWTUtil.generateToken(jwtSecret, jwtExpirationTime, account, httpRequest));
            bodyResponse.put("refresh_token", JWTUtil.generateToken(jwtSecret, refreshTokenExpirationTime, account, httpRequest));
            response.setContentType(APPLICATION_JSON_VALUE);
            new ObjectMapper().writeValue(response.getOutputStream(), bodyResponse);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, String> bodyResponse = new HashMap<>();
        response.setContentType(APPLICATION_JSON_VALUE);
        try {
            bodyResponse = accountService.getTokensWithRefreshToken(request);
        } catch (Exception e) {
            response.setStatus(FORBIDDEN.value());
            bodyResponse.put("error_message", e.getMessage());
        } finally {
            new ObjectMapper().writeValue(response.getOutputStream(), bodyResponse);
        }
    }
}
