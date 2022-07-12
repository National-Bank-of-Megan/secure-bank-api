package pl.edu.pw.service.account;

import com.auth0.jwt.exceptions.TokenExpiredException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.edu.pw.auth.logic.PasswordHashesGenerator;
import pl.edu.pw.domain.Account;
import pl.edu.pw.domain.AccountDetails;
import pl.edu.pw.domain.AccountHash;
import pl.edu.pw.domain.Device;
import pl.edu.pw.dto.AccountRegistration;
import pl.edu.pw.dto.PartPasswordHash;
import pl.edu.pw.dto.VerifyCodeRequest;
import pl.edu.pw.repository.AccountRepository;
import pl.edu.pw.service.otp.OtpService;
import pl.edu.pw.util.JWTUtil;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.*;
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
    public String registerAccount(AccountRegistration registerData) {
        String rawPassword = registerData.getPassword();
        registerData.setPassword(passwordEncoder.encode(registerData.getPassword()));
        List<Account> allAccounts = accountRepository.findAll();
        Set<String> existingAccountsNumbers = allAccounts.stream().map(Account::getAccountNumber).collect(Collectors.toSet());
        Set<String> existingClientIds = allAccounts.stream().map(Account::getClientId).collect(Collectors.toSet());
        Account accountToRegister = AccountMapper.map(registerData, existingAccountsNumbers, existingClientIds);
        setAccountHashes(accountToRegister, rawPassword);
        accountToRegister.setAccountDetails(new AccountDetails(null, null, registerData.getEmail(), null));
        accountToRegister.addDevice(new Device("TODO", registerData.getPublicIp()));
        String secret = otpService.generateSecret();
        accountToRegister.setSecret(secret);
        accountRepository.save(accountToRegister);
        return otpService.getUriForImage(secret);
    }

    private void setAccountHashes(Account accountToRegister, String rawPassword) {
        List<PartPasswordHash> partPasswordHashes = PasswordHashesGenerator.generatePasswordHashes(rawPassword, passwordEncoder);
        List<AccountHash> accountHashes = partPasswordHashes.stream().map(AccountHashMapper::map).toList();
        accountToRegister.addAllAccountHashes(accountHashes);
        accountToRegister.setCurrentAuthenticationHash(accountHashes.get(0));
    }

    @Override
    public Account getAccount(String accountNumber) {
        return accountRepository.findByClientId(accountNumber).orElse(null);
    }

    @Override
    public boolean verify(VerifyCodeRequest verifyRequest, HttpServletRequest httpRequest) {
//        todo check whether devices are the same (login and verification)
        Account account  = accountRepository.findByClientId(verifyRequest.getClientId()).orElse(null);
        if(account == null) return false;
        else {
            //      jak bd coś takiego spr. to można dawać komunikaty o podejrzanej aktywności
            if(!account.isShouldBeVerified()) return  false;

            boolean isCodeValid = otpService.verifyCode(verifyRequest.getCode(),account.getSecret());
            if (!isCodeValid) return false;

            account.setShouldBeVerified(false);
            accountRepository.save(account);
            return true;
        }
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
        Account account = accountRepository.findByClientId(clientId).orElse(null);
        return account != null ? account.getCurrentAuthenticationHash().getPasswordPartCharactersPosition()
                : (NO_SUCH_ACCOUNT_MESSAGE + " with client id " + clientId);
    }

    public static class AccountMapper {
        public static Account map(AccountRegistration registerData, Set<String> existingAccountsNumbers, Set<String> existingClientIds) {
            return new Account(
                existingClientIds,
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
        return accountRepository.findByClientId(clientId).
                orElseThrow(() -> new UsernameNotFoundException(String.format("Client %s not found", clientId)));
    }

}
