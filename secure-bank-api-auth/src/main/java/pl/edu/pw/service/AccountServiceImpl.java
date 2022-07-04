package pl.edu.pw.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.edu.pw.domain.Otp;
import pl.edu.pw.dto.AccountRegistration;
import pl.edu.pw.dto.LoginRequest;
import pl.edu.pw.dto.VerifyCodeRequest;
import pl.edu.pw.repository.AccountRepository;
import pl.edu.pw.repository.OtpRepository;
import pl.edu.pw.service.otp.OtpService;
import pl.edu.pw.user.Account;

import javax.transaction.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;


@Service
@Transactional
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService, UserDetailsService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;

    @Override
    public Account getAccount(String accountNumber) {
        return accountRepository.findByClientId(Long.valueOf(accountNumber)).orElse(null);
    }


    @Override
    public Map<String, String> verify(VerifyCodeRequest request) {
        otpService.verify(request.getCode());

        Account account = accountRepository.findByClientId(Long.valueOf(request.getClientId())).orElseThrow(
                ()-> new UsernameNotFoundException("user not found")
        );
        otpService.clearOneTimePassword(account);
        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
        String token = JWT.create()
                .withSubject(account.getClientId().toString())
                .withExpiresAt(new Date(System.currentTimeMillis()+1000*60*60))
                .withIssuer("/api/account/verify")
                .sign(algorithm);

        String refreshToken = JWT.create().withSubject(account.getClientId().toString())
                .withExpiresAt(new Date(System.currentTimeMillis()+1000*120*60))
                .withIssuer("/api/account/verify")
                .sign(algorithm);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token",token);
        tokens.put("refresh_token",refreshToken);
       return tokens;

    }

    @Override
    public UserDetails loadUserByUsername(String clientId) throws UsernameNotFoundException {
        return accountRepository.findByClientId(Long.valueOf(clientId)).
                orElseThrow(() -> new UsernameNotFoundException(String.format("Client %s not found", clientId)));
    }

}
