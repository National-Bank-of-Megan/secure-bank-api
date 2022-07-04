package pl.edu.pw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.edu.pw.dto.AccountRegistration;
import pl.edu.pw.dto.LoginRequest;
import pl.edu.pw.dto.VerifyCodeRequest;
import pl.edu.pw.repository.AccountRepository;
import pl.edu.pw.user.Account;

import javax.transaction.Transactional;

import java.util.Set;
import java.util.stream.Collectors;


@Service
@Transactional
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService, UserDetailsService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    public Account getAccount(String accountNumber) {
        return accountRepository.findByClientId(Long.valueOf(accountNumber)).orElse(null);
    }

//    @Override
//    public void login(LoginRequest request) {
//        Authentication authentication = authenticationManager
//                .authenticate(new UsernamePasswordAuthenticationToken(request.getClintId(), request.getPassword()));
//
//    }

    @Override
    public String verify(VerifyCodeRequest request) {
        return null;
    }

    @Override
    public UserDetails loadUserByUsername(String clientId) throws UsernameNotFoundException {
        return accountRepository.findByClientId(Long.valueOf(clientId)).
                orElseThrow(() -> new UsernameNotFoundException(String.format("Client %s not found", clientId)));
    }

}
