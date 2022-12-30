package pl.edu.pw.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.edu.pw.domain.Account;
import pl.edu.pw.service.account.AccountService;
import pl.edu.pw.service.account.AuthService;


@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final AuthService authService;
    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String name = authentication.getName();
        String password = authentication.getCredentials().toString();
        System.out.println("provider credentias: "+authentication.getAuthorities().toString());
        Account account = authService.getAccount(name);
        if(RestAuthenticationFailureHandler.isAccountLocked(account)){
            if(RestAuthenticationFailureHandler.isAccountStillLocked(account)){
                return null;
            } else {
                accountService.unlockAccount(account);
            }
        }
            String hashedPasswordPart = account.getCurrentAuthenticationHash().getPasswordPart();
            if (passwordEncoder.matches(password, hashedPasswordPart)) {
                return new UsernamePasswordAuthenticationToken(account, authentication.getAuthorities());
            }
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
