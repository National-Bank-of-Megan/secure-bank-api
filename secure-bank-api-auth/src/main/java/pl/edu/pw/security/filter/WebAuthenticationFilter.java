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
import pl.edu.pw.repository.AccountHashRepository;
import pl.edu.pw.repository.AccountRepository;
import pl.edu.pw.service.devices.DevicesServiceImpl;
import pl.edu.pw.service.email.EmailSenderServiceImpl;
import pl.edu.pw.service.otp.OtpService;
import pl.edu.pw.util.JWTUtil;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class WebAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final Logger log = LoggerFactory.getLogger(WebAuthenticationFilter.class);
    private final AuthenticationManager authenticationManager;
    private final AccountRepository accountRepository;
    private final AccountHashRepository accountHashRepository;
    private final DevicesServiceImpl devicesService;
    private final SecureRandom random;
    private final OtpService otpService;
    private final EmailSenderServiceImpl emailSenderService;

    private long jwtExpirationTime;
    private long refreshTokenExpirationTime;
    private String jwtSecret;

    public WebAuthenticationFilter(AuthenticationManager authenticationManager, AccountRepository accountRepository,
                                   AccountHashRepository accountHashRepository, DevicesServiceImpl devicesService,
                                   OtpService otpService, EmailSenderServiceImpl emailSenderService, long jwtExpirationTime, long refreshTokenExpirationTime, String jwtSecret) {

        this.authenticationManager = authenticationManager;
        this.accountRepository = accountRepository;
        this.accountHashRepository = accountHashRepository;
        this.devicesService = devicesService;
        this.otpService = otpService;
        this.emailSenderService = emailSenderService;
        this.jwtExpirationTime = jwtExpirationTime;
        this.refreshTokenExpirationTime = refreshTokenExpirationTime;
        this.jwtSecret = jwtSecret;
        this.random = new SecureRandom();
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        log.info("WebAuthenticationFilter->\ttrying to authenticate...");
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
        String ipAddress = devicesService.getIpAddress(request);
        log.info("Machine trying to access api: " + ipAddress);

//        todo integracja z serwisem urządzeń
        boolean isNewDevice = false;

        Account account = (Account) authResult.getPrincipal();
        if (isNewDevice) {
            emailSenderService.send(account.getUsername(), otpService.generateOneTimePassword(account));
        } else {
            Account fetchedAccount = accountRepository.findByAccountNumber(account.getAccountNumber()).get();
            List<AccountHash> allByAccountAccountNumber = accountHashRepository.findAllByAccountAccountNumber(account.getAccountNumber());
            setOtherHashCombination(fetchedAccount, allByAccountAccountNumber);

            Map<String, String> tokens = new HashMap<>();
            tokens.put("access_token", JWTUtil.generateToken(jwtSecret, jwtExpirationTime, account, request));
            tokens.put("refresh_token", JWTUtil.generateToken(jwtSecret, refreshTokenExpirationTime, account, request));
            response.setContentType(APPLICATION_JSON_VALUE);
            new ObjectMapper().writeValue(response.getOutputStream(), tokens);
        }
    }

    private void setOtherHashCombination(Account account, List<AccountHash> accountHashList) {
        AccountHash currentAccountHash = account.getCurrentAuthenticationHash();
        boolean otherAccountHash = false;
        do {
            int index = random.nextInt(0, accountHashList.size());
            AccountHash accountHash = accountHashList.get(index);
            if (!currentAccountHash.getId().equals(accountHash.getId())) {
                otherAccountHash = true;
                account.setCurrentAuthenticationHash(accountHash);
                accountRepository.save(account);
            }
        } while (!otherAccountHash);
    }
}
