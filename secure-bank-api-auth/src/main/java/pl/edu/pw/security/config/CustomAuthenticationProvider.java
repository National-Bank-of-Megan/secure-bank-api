package pl.edu.pw.security.config;

import lombok.RequiredArgsConstructor;
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
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String name = authentication.getName();
        String password = authentication.getCredentials().toString();
        Account account = authService.getAccount(name);
        if (account != null) {
            String hashedPasswordPart = account.getCurrentAuthenticationHash().getPasswordPart();
            System.out.println(hashedPasswordPart);
//            if (passwordEncoder.matches(password, hashedPasswordPart)) {
                if (passwordEncoder.matches(password, account.getPassword())) { // temporary for easier testing
                    return new UsernamePasswordAuthenticationToken(account, new ArrayList<>());
//                }
            }
        }
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
