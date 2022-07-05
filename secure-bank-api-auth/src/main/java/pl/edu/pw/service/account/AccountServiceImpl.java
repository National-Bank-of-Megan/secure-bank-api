package pl.edu.pw.service.account;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.edu.pw.auth.logic.PasswordHashesGenerator;
import pl.edu.pw.dto.AccountRegistration;
import pl.edu.pw.dto.PartPasswordHash;
import pl.edu.pw.dto.VerifyCodeRequest;
import pl.edu.pw.repository.AccountRepository;
import pl.edu.pw.service.otp.OtpService;
import pl.edu.pw.user.Account;
import pl.edu.pw.user.AccountHash;

import javax.transaction.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@Transactional
@RequiredArgsConstructor


public class AccountServiceImpl implements AccountService, UserDetailsService {
    private static final String NO_SUCH_ACCOUNT_MESSAGE = "No such account";

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;

    @Override
    public void registerAccount(AccountRegistration registerData) {
        String rawPassword = registerData.getPassword();
        registerData.setPassword(passwordEncoder.encode(registerData.getPassword()));
        Set<String> accountsNumbers = accountRepository.findAll().stream().map(Account::getAccountNumber).collect(Collectors.toSet());
        Account accountToRegister = AccountMapper.map(registerData, accountsNumbers);
        List<PartPasswordHash> partPasswordHashes = PasswordHashesGenerator.generatePasswordHashes(rawPassword, passwordEncoder);
        List<AccountHash> accountHashes = partPasswordHashes.stream().map(AccountHashMapper::map).toList();
        accountToRegister.addAllAccountHashes(accountHashes);
        accountToRegister.setCurrentAuthenticationHash(accountHashes.get(0));
        accountRepository.save(accountToRegister);
    }

    @Override
    public Account getAccount(String accountNumber) {
        return accountRepository.findByClientId(Long.valueOf(accountNumber)).orElse(null);
    }

    @Override
    public Map<String, String> verify(VerifyCodeRequest request) {
        otpService.verify(request.getCode());

        Account account = accountRepository.findByClientId(Long.valueOf(request.getClientId())).orElseThrow(
                () -> new UsernameNotFoundException("user not found")
        );
        otpService.clearOneTimePassword(account);
        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
        String token = JWT.create()
                .withSubject(account.getClientId().toString())
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .withIssuer("/api/account/verify")
                .sign(algorithm);

        String refreshToken = JWT.create().withSubject(account.getClientId().toString())
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 120 * 60))
                .withIssuer("/api/account/verify")
                .sign(algorithm);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", token);
        tokens.put("refresh_token", refreshToken);
        return tokens;
    }

    public String getLoginCombination(String username) {
        Account account = accountRepository.findByAccountNumber(username).orElse(null);
        return account != null ? account.getCurrentAuthenticationHash().getPasswordPartCharactersPosition()
                               : (NO_SUCH_ACCOUNT_MESSAGE + " with username " + username);
    }

    @Override
    public Account getAccountLazy(String accountNumber) {
        return accountRepository.getAccountLazy(accountNumber);
    }

    public static class AccountMapper {
        public static Account map(AccountRegistration registerData, Set<String> existingAccountsNumbers) {
            return new Account(
                existingAccountsNumbers,
                registerData.getPassword()
            );
        }
    }

    public static class AccountHashMapper {
        public static AccountHash map(PartPasswordHash partPasswordHash) {
            List<String> hashLocations = partPasswordHash.getDigitsLocations().stream().map(String::valueOf).collect(Collectors.toList());
            String passwordPartCharactersPosition = String.join(" ", hashLocations);
            return new AccountHash(
                partPasswordHash.getHash(),
                passwordPartCharactersPosition
            );
        }
    }

    @Override
    public UserDetails loadUserByUsername(String clientId) throws UsernameNotFoundException {
        return accountRepository.findByClientId(Long.valueOf(clientId)).
                orElseThrow(() -> new UsernameNotFoundException(String.format("Client %s not found", clientId)));
    }

}
