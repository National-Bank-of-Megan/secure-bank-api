package pl.edu.pw.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.edu.pw.service.AccountService;
import pl.edu.pw.user.Account;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String name = authentication.getName();
        String password = authentication.getCredentials().toString();
        Account account = accountService.getAccount(name);
        if (account != null) {
            String hashedPasswordPart = account.getCurrentAuthenticationHash().getPasswordPart();
            System.out.println(hashedPasswordPart);
            if (passwordEncoder.matches(password, hashedPasswordPart)) {
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
