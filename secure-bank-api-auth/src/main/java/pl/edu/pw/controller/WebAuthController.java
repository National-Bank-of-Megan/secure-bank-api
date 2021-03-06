package pl.edu.pw.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.pw.domain.Account;
import pl.edu.pw.domain.JsonWebTokenType;
import pl.edu.pw.dto.AccountRegistration;
import pl.edu.pw.dto.LoginCombinationDto;
import pl.edu.pw.dto.SuccessfulRegistrationResponse;
import pl.edu.pw.dto.VerifyCodeRequest;
import pl.edu.pw.repository.AccountRepository;
import pl.edu.pw.service.account.AuthService;
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
@Log4j2
public class WebAuthController {

    private final AuthService authService;
    private final AccountRepository accountRepository;
    private final JWTUtil jwtUtil;

    @PostMapping(value = "/register", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<SuccessfulRegistrationResponse> register(@Valid @RequestBody AccountRegistration registration, HttpServletRequest request) {
        registration.setLocalIp(getLocalIpAddress());
        registration.setPublicIp(HttpRequestUtils.getClientIpAddressFromRequest(request));
        SuccessfulRegistrationResponse registerResponseData = authService.registerAccount(registration);
        return ResponseEntity.created(URI.create("/register")).body(registerResponseData);
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
    public LoginCombinationDto getLoginCombination(@Valid @RequestParam String clientId) {
        String combination = authService.getLoginCombination(clientId);
        return new LoginCombinationDto(clientId, combination);
    }

    @PostMapping("/login/verify")
    public ResponseEntity<?> verifyCode(@Valid @RequestBody VerifyCodeRequest request, HttpServletRequest httpRequest, HttpServletResponse response) throws IOException {

        if (authService.verify(request, httpRequest)) {
            Account account = accountRepository.findById(request.getClientId()).orElse(null);
            Map<String, String> bodyResponse = new HashMap<>();
            bodyResponse.put("access_token", jwtUtil.getToken(account, httpRequest, JsonWebTokenType.ACCESS));
            bodyResponse.put("refresh_token", jwtUtil.getToken(account, httpRequest, JsonWebTokenType.REFRESH));

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
            bodyResponse = jwtUtil.getAuthTokenByRefreshToken(request);
        } catch (Exception e) {
            response.setStatus(FORBIDDEN.value());
            bodyResponse.put("error_message", e.getMessage());
        } finally {
            new ObjectMapper().writeValue(response.getOutputStream(), bodyResponse);
        }
    }
}
