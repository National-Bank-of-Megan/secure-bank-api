package pl.edu.pw.security.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pl.edu.pw.repository.AccountHashRepository;
import pl.edu.pw.repository.AccountRepository;
import pl.edu.pw.service.devices.DevicesService;
import pl.edu.pw.service.devices.DevicesServiceImpl;
import pl.edu.pw.service.email.EmailSenderServiceImpl;
import pl.edu.pw.service.otp.OtpService;
import pl.edu.pw.user.Account;
import pl.edu.pw.user.AccountHash;
import pl.edu.pw.util.JWTUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@AllArgsConstructor
public class WebAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final Logger log = LoggerFactory.getLogger(WebAuthenticationFilter.class);
    private AuthenticationManager authenticationManager;
    private AccountRepository accountRepository;
    private AccountHashRepository accountHashRepository;
//    todo nazmienic na DevicesService
    private DevicesServiceImpl devicesService;
    private SecureRandom random;
    private OtpService otpService;
    private EmailSenderServiceImpl emailSenderService;

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
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        log.info("WebAuthenticationFilter->\tsending JWT. Authentication successful");
        String ipAddress = devicesService.getIpAddress(request);
        log.info("Machine trying to access api: "+ ipAddress);

//        todo integracja z serwisem urządzeń
        boolean isNewDevice = false;

        Account account = (Account) authResult.getPrincipal();
        if(isNewDevice){
            emailSenderService.send(account.getUsername(),otpService.generateOneTimePassword(account));
        }else{
            Account eagerAccount = accountRepository.findByAccountNumber(account.getAccountNumber()).get();
            List<AccountHash> allByAccountAccountNumber = accountHashRepository.findAllByAccountAccountNumber(account.getAccountNumber());
            setOtherHashCombination(eagerAccount, allByAccountAccountNumber);

//            jwt generation
            int refreshToken = 1000 * 120 * 60;
            int accessToken = 1000 * 60 * 60;

            Map<String, String> tokens = new HashMap<>();
            tokens.put("access_token", JWTUtil.generateToken(account,accessToken,request));
            tokens.put("refresh_token", JWTUtil.generateToken(account,refreshToken,request));
            response.setContentType(APPLICATION_JSON_VALUE);
            new ObjectMapper().writeValue(response.getOutputStream(), tokens);
        }
    }

    private void setOtherHashCombination(Account account, List<AccountHash> accountHashList) {
//        List<AccountHash> accountHashList = account.getAccountHashList();
//        Hibernate.initialize(accountHashList);
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
