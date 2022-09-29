package pl.edu.pw.security.config;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.edu.pw.domain.Account;
import pl.edu.pw.service.account.AuthService;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class CustomMobileAuthenticationProvider implements AuthenticationProvider {

    private final AuthService authService;
    private  final PasswordEncoder passwordEncoder;
    private static final Logger log = LoggerFactory.getLogger(CustomMobileAuthenticationProvider.class);

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.info("Authentication for mobile app");
        String name = authentication.getName();
        String password = authentication.getCredentials().toString();
        Account account = authService.getAccount(name);
        if (account != null) {
            if (passwordEncoder.matches(password, account.getPassword())) {
                return new UsernamePasswordAuthenticationToken(account, new ArrayList<>());
            }
        }
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
