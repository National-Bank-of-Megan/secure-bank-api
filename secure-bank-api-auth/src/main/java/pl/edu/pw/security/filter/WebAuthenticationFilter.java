package pl.edu.pw.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pl.edu.pw.domain.Account;
import pl.edu.pw.domain.AccountHash;
import pl.edu.pw.domain.Device;
import pl.edu.pw.domain.JsonWebTokenType;
import pl.edu.pw.repository.AccountHashRepository;
import pl.edu.pw.repository.AccountRepository;
import pl.edu.pw.service.account.AuthService;
import pl.edu.pw.service.devices.DevicesService;
import pl.edu.pw.util.JWTUtil;
import pl.edu.pw.util.http.HttpRequestUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class WebAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final Logger log = LoggerFactory.getLogger(WebAuthenticationFilter.class);
    private final AuthenticationManager authenticationManager;
    private final AccountRepository accountRepository;
    private final DevicesService devicesService;
    private final SecureRandom random;
    private final JWTUtil jwtUtil;
    private final EntityManager entityManager;
    private final AuthService authService;

    public WebAuthenticationFilter(AuthenticationManager authenticationManager, AccountRepository accountRepository,
                                   DevicesService devicesService, JWTUtil jwtUtil, EntityManager entityManager,
                                   AuthService authService) {

        this.authenticationManager = authenticationManager;
        this.accountRepository = accountRepository;
        this.devicesService = devicesService;
        this.authService = authService;
        this.random = new SecureRandom();
        this.jwtUtil = jwtUtil;
        this.entityManager = entityManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("WebAuthenticationFilter->\ttrying to authenticate...");
        String deviceFingerprint = request.getHeader("Device-Fingerprint");
        if (deviceFingerprint == null) {
            throw new RuntimeException("Device-Fingerprint header is required to log in");
        }
        String clientId, password;
        try {
            Map<String, String> requestMap = new ObjectMapper().readValue(request.getInputStream(), Map.class);
            clientId = requestMap.get("clientId");
            password = requestMap.get("password");
        } catch (IOException e) {
            throw new AuthenticationServiceException(e.getMessage(), e);
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(clientId, password);
        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
        log.info("WebAuthenticationFilter->\tsending JWT. Authentication successful");
        String deviceFingerprint = request.getHeader("Device-Fingerprint");
        String ipAddress = HttpRequestUtils.getClientIpAddressFromRequest(request);
        log.info("Machine trying to access api: " + ipAddress);
        String loggedClientId = ((Account)authResult.getPrincipal()).getClientId();
        TypedQuery<Device> deviceQuery = entityManager.createQuery(
                "SELECT d FROM Device d JOIN FETCH d.account WHERE d.account.clientId = :loggedClientId", Device.class);
        List<Device> accountDevices = deviceQuery.setParameter("loggedClientId", loggedClientId).getResultList();

        if (isUntrustedDevice(accountDevices, deviceFingerprint)) {
            response.setStatus(206);
        } else {
            Account account = accountRepository.findByAccountNumber(((Account)authResult.getPrincipal()).getAccountNumber()).orElseThrow(
                    () -> new RuntimeException("Something went wrong with fetching your account")
            );
            authService.setOtherHashCombination(account, random);
            Device loggedDevice = accountDevices.stream().filter(device -> device.getFingerprint().equals(deviceFingerprint))
                    .findFirst().get();
            devicesService.updateDeviceLogInDate(loggedDevice);
            Map<String, String> tokens = new HashMap<>();
            tokens.put("access_token", jwtUtil.getToken(account, request, JsonWebTokenType.ACCESS));
            tokens.put("refresh_token", jwtUtil.getToken(account, request, JsonWebTokenType.REFRESH));
            response.setContentType(APPLICATION_JSON_VALUE);
            new ObjectMapper().writeValue(response.getOutputStream(), tokens);
        }
    }

    private boolean isUntrustedDevice(List<Device> accountDevices, String deviceFingerprint) {
        return accountDevices.stream().noneMatch(device -> device.getFingerprint().equals(deviceFingerprint));
    }
}
