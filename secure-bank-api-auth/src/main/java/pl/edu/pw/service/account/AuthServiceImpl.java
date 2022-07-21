package pl.edu.pw.service.account;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.edu.pw.auth.logic.PasswordHashesGenerator;
import pl.edu.pw.domain.*;
import pl.edu.pw.dto.AccountRegistration;
import pl.edu.pw.dto.PartPasswordHash;
import pl.edu.pw.dto.SuccessfulRegistrationResponse;
import pl.edu.pw.dto.VerifyCodeRequest;
import pl.edu.pw.repository.AccountRepository;
import pl.edu.pw.service.otp.OtpService;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService, UserDetailsService {
    private static final String NO_SUCH_ACCOUNT_MESSAGE = "No such account";


    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;

    @Override
    public SuccessfulRegistrationResponse registerAccount(AccountRegistration registerData) {
        if (accountRepository.findByAccountDetailsEmail(registerData.getEmail()).isPresent()) {
            throw new IllegalArgumentException("This email is already taken.");
        }
        String rawPassword = registerData.getPassword();
        log.info("Getting raw password");
        registerData.setPassword(passwordEncoder.encode(registerData.getPassword()));
        List<Account> allAccounts = accountRepository.findAll();
        log.info("getting existsing account numbers");
        Set<String> existingAccountsNumbers = allAccounts.stream().map(Account::getAccountNumber).collect(Collectors.toSet());
        log.info("getting existsing client ids");
        Set<String> existingClientIds = allAccounts.stream().map(Account::getClientId).collect(Collectors.toSet());
        log.info("mapping account pl.edu.pw.dto");
        Account accountToRegister = AccountMapper.map(registerData, existingAccountsNumbers, existingClientIds);
        log.info("setting password hashes");
        setAccountHashes(accountToRegister, rawPassword);
        log.info("adding subaccounts");
        accountToRegister.addSubAccounts(Currency.values());

        log.info("setting account details");
        accountToRegister.setAccountDetails(new AccountDetails(registerData.getFirstName(), registerData.getLastName(), registerData.getEmail(), null));
        log.info("adding new device");
        accountToRegister.addDevice(new Device("TODO", registerData.getPublicIp()));
        log.info("generating otp secret");
        String secret = otpService.generateSecret();
        log.info("setting otp secret");
        accountToRegister.setSecret(secret);
        log.info("saving account");
        String generatedClientId = accountRepository.save(accountToRegister).getClientId();
        log.info("getting url for qr code image");
        otpService.getUriForImage(secret);
        String qrImageUri = otpService.getUriForImage(secret);
        return new SuccessfulRegistrationResponse(generatedClientId, qrImageUri);
    }

    private void setAccountHashes(Account accountToRegister, String rawPassword) {
        List<PartPasswordHash> partPasswordHashes = PasswordHashesGenerator.generatePasswordHashes(rawPassword, passwordEncoder);
        List<AccountHash> accountHashes = partPasswordHashes.stream().map(AccountHashMapper::map).toList();
        accountToRegister.addAllAccountHashes(accountHashes);
        accountToRegister.setCurrentAuthenticationHash(accountHashes.get(0));
    }

    @Override
    public Account getAccount(String accountNumber) {
        return accountRepository.findById(accountNumber).orElse(null);
    }

    @Override
    public boolean verify(VerifyCodeRequest verifyRequest, HttpServletRequest httpRequest) {
//        todo check whether devices are the same (login and verification)
        Account account = accountRepository.findById(verifyRequest.getClientId()).orElse(null);
        if (account == null) return false;
        else {
            //      jak bd coś takiego spr. to można dawać komunikaty o podejrzanej aktywności
            if (!account.isShouldBeVerified()) return false;

            boolean isCodeValid = otpService.verifyCode(verifyRequest.getCode(), account.getSecret());
            if (!isCodeValid) return false;

            account.setShouldBeVerified(false);
            accountRepository.save(account);
            return true;
        }
    }

    public String getLoginCombination(String clientId) {
        Account account = accountRepository.findById(clientId).orElse(null);
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
        return accountRepository.findById(clientId).
                orElseThrow(() -> new UsernameNotFoundException(String.format("Client %s not found", clientId)));
    }

}
