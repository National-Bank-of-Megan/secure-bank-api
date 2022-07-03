package pl.edu.pw.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.edu.pw.repository.AccountRepository;
import pl.edu.pw.user.Account;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String name = authentication.getName();
        String password = authentication.getCredentials().toString();
        Optional<Account> account = accountRepository.findByClientId(Long.valueOf(name));
        if (account.isPresent()) {
            Account fetchedAccount = account.get();
            String hashedPassword = fetchedAccount.getPassword();
            if (passwordEncoder.matches(password, hashedPassword)) {
                return new UsernamePasswordAuthenticationToken(fetchedAccount, new ArrayList<>());
            }
        }
        return null;
    }
//        Set<String> accountsNumbers = accountRepository.findAll().stream().map(Account::getClientId).collect(Collectors.toSet());
//        if (account.isPresent()) {
//            int passwordHashId = Session.get("hashId");
//            Hash hash = hashRepository.getHash(passwordHashId);
//            if (passwordEncoder.matches(password, hash)) {
//                return new UsernamePasswordAuthenticationToken(
//                        new Account(Long.valueOf(name), password), new ArrayList<>());
//            }
//        }
//        return null;
//    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
