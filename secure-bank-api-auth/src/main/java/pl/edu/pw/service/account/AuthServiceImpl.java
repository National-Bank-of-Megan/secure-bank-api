package pl.edu.pw.service.account;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.edu.pw.domain.Account;
import pl.edu.pw.domain.AccountDetails;
import pl.edu.pw.domain.AccountHash;
import pl.edu.pw.domain.Currency;
import pl.edu.pw.domain.Device;
import pl.edu.pw.dto.AccountRegistration;
import pl.edu.pw.dto.PartPasswordHash;
import pl.edu.pw.dto.SuccessfulRegistrationResponse;
import pl.edu.pw.exception.ResourceNotFoundException;
import pl.edu.pw.repository.AccountHashRepository;
import pl.edu.pw.repository.AccountRepository;
import pl.edu.pw.service.devices.DevicesService;
import pl.edu.pw.service.otp.OtpService;
import pl.edu.pw.util.PasswordUtil;
import pl.edu.pw.util.http.HttpRequestUtils;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService, UserDetailsService {
    private static final String NO_SUCH_ACCOUNT_MESSAGE = "No such account";

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final DevicesService devicesService;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final AccountHashRepository accountHashRepository;

    @Override
    public SuccessfulRegistrationResponse registerAccount(AccountRegistration registerData, HttpServletRequest request) {
        if (accountRepository.findByAccountDetailsEmail(registerData.getEmail()).isPresent()) {
            throw new IllegalArgumentException("This email is already taken");
        }

        String deviceName = HttpRequestUtils.getDeviceNameFromRequest(request, devicesService);
        String rawPassword = registerData.getPassword();
        registerData.setPassword(passwordEncoder.encode(registerData.getPassword()));
        List<Account> allAccounts = accountRepository.findAll();
        Set<String> existingAccountsNumbers = allAccounts.stream().map(Account::getAccountNumber).collect(Collectors.toSet());
        Set<String> existingClientIds = allAccounts.stream().map(Account::getClientId).collect(Collectors.toSet());
        Account accountToRegister = AccountMapper.map(registerData, existingAccountsNumbers, existingClientIds);
        PasswordUtil.addAccountHashes(accountToRegister, rawPassword, passwordEncoder);

        accountToRegister.addSubAccounts(Currency.values());
        accountToRegister.setAccountDetails(new AccountDetails(registerData.getFirstName(), registerData.getLastName(), registerData.getEmail(), "666 666 666"));

        accountToRegister.addDevice(new Device(registerData.getDeviceFingerprint(), deviceName, LocalDateTime.now(), registerData.getIp()));

        String secret = otpService.generateSecret();
        accountToRegister.setSecret(secret);

        String generatedClientId = accountRepository.save(accountToRegister).getClientId();

        otpService.getUriForImage(secret);
        String qrImageUri = otpService.getUriForImage(secret);
        return new SuccessfulRegistrationResponse(generatedClientId, qrImageUri);
    }

    @Override
    public Account getAccount(String accountNumber) {
        return accountRepository.findById(accountNumber).orElse(null);
    }

    @Override
    public void setOtherHashCombination(Account account, SecureRandom random) {
        List<AccountHash> accountHashList = accountHashRepository.findAllByAccountAccountNumber(account.getAccountNumber());
        AccountHash currentAccountHash = account.getCurrentAuthenticationHash();
        boolean otherAccountHash = false;
        do {
            int index = random.nextInt(0, accountHashList.size());
            AccountHash accountHash = accountHashList.get(index);
            if (!currentAccountHash.getId().equals(accountHash.getId())) {
                otherAccountHash = true;
                account.setCurrentAuthenticationHash(accountHash);
                accountRepository.save(account);
            }
        } while (!otherAccountHash);
    }

    public String getLoginCombination(String clientId) {
        Account account = accountRepository.findById(clientId).
                orElseThrow(() -> new ResourceNotFoundException(NO_SUCH_ACCOUNT_MESSAGE + " with client id " + clientId));

        return account.getCurrentAuthenticationHash().getPasswordPartCharactersPosition();
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

    @Override
    public Account loadUserByUsername(String clientId) throws UsernameNotFoundException {
        return accountRepository.findById(clientId).
                orElseThrow(() -> new UsernameNotFoundException(String.format("Account %s not found", clientId)));
    }
}
