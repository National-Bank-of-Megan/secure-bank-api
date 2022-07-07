package pl.edu.pw.service.account;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.edu.pw.auth.logic.PasswordHashesGenerator;
import pl.edu.pw.domain.Account;
import pl.edu.pw.domain.AccountHash;
import pl.edu.pw.dto.AccountRegistration;
import pl.edu.pw.dto.PartPasswordHash;
import pl.edu.pw.dto.VerifyCodeRequest;
import pl.edu.pw.repository.AccountRepository;
import pl.edu.pw.service.otp.OtpService;
import pl.edu.pw.util.JWTUtil;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;


@Service
@Transactional
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService, UserDetailsService {
    private static final String NO_SUCH_ACCOUNT_MESSAGE = "No such account";

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;

    @Value("${jwt.expirationTime}")
    private long jwtExpirationTime;

    @Value("${refreshToken.expirationTime}")
    private long refreshTokenExpirationTime;

    @Value("${jwt.secret}")
    private String jwtSecret;

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
        accountToRegister.setClientId(123456L);
        accountRepository.save(accountToRegister);
    }

    @Override
    public Account getAccount(String accountNumber) {
        return accountRepository.findByClientId(Long.valueOf(accountNumber)).orElse(null);
    }

    @Override
    public Map<String, String> verify(VerifyCodeRequest verifyRequest, HttpServletRequest httpRequest) {
        otpService.verify(verifyRequest.getCode());

        Account account = accountRepository.findByClientId(Long.valueOf(verifyRequest.getClientId())).orElseThrow(
                () -> new UsernameNotFoundException("User not found")
        );
        otpService.clearOneTimePassword(account);
        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", JWTUtil.generateToken(jwtSecret, jwtExpirationTime, account, httpRequest));
        tokens.put("refresh_token", JWTUtil.generateToken(jwtSecret, refreshTokenExpirationTime, account, httpRequest));

        return tokens;
    }

    @Override
    public Map<String, String> getTokensWithRefreshToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            Account account = (Account) SecurityContextHolder.getContext().getAuthentication();
            Map<String, String> tokens = new HashMap<>();
            tokens.put("access_token", JWTUtil.generateToken(jwtSecret, jwtExpirationTime, account, request));
            tokens.put("refresh_token", JWTUtil.generateToken(jwtSecret, refreshTokenExpirationTime, account, request));
            return tokens;
        } else {
            throw new RuntimeException("There is no \"Bearer\" header in your request.");
        }
    }

    public String getLoginCombination(String clientId) {
        Account account = accountRepository.findByClientId(Long.valueOf(clientId)).orElse(null);
        return account != null ? account.getCurrentAuthenticationHash().getPasswordPartCharactersPosition()
                : (NO_SUCH_ACCOUNT_MESSAGE + " with client id " + clientId);
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

    // TODO: do wywalenia?
    @Override
    public UserDetails loadUserByUsername(String clientId) throws UsernameNotFoundException {
        return accountRepository.findByClientId(Long.valueOf(clientId)).
                orElseThrow(() -> new UsernameNotFoundException(String.format("Client %s not found", clientId)));
    }

}
