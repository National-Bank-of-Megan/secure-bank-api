package pl.edu.pw.auth.service.account;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.edu.pw.auth.dto.AccountRegistration;
import pl.edu.pw.auth.dto.PartPasswordHash;
import pl.edu.pw.auth.dto.SuccessfulRegistrationResponse;
import pl.edu.pw.auth.exception.ResourceNotFoundException;
import pl.edu.pw.auth.repository.AccountHashRepository;
import pl.edu.pw.auth.repository.AccountRepository;
import pl.edu.pw.auth.service.devices.DevicesService;
import pl.edu.pw.core.domain.Account;
import pl.edu.pw.core.domain.AccountDetails;
import pl.edu.pw.core.domain.AccountHash;
import pl.edu.pw.core.domain.Currency;
import pl.edu.pw.auth.service.otp.OtpService;
import pl.edu.pw.auth.util.PasswordUtil;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.security.SecureRandom;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class AuthServiceImpl implements AuthService, UserDetailsService {

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

        String rawPassword = registerData.getPassword();
        registerData.setPassword(passwordEncoder.encode(registerData.getPassword()));
        List<Account> allAccounts = accountRepository.findAll();
        Set<String> existingAccountsNumbers = allAccounts.stream().map(Account::getAccountNumber).collect(Collectors.toSet());
        Set<String> existingClientIds = allAccounts.stream().map(Account::getClientId).collect(Collectors.toSet());
        Account accountToRegister = AccountMapper.map(registerData, existingAccountsNumbers, existingClientIds);
        PasswordUtil.addAccountHashes(accountToRegister, rawPassword, passwordEncoder);

        accountToRegister.addSubAccounts(Currency.values());
        accountToRegister.setAccountDetails(new AccountDetails(registerData.getFirstName(), registerData.getLastName(), registerData.getEmail(), "666 666 666"));

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
                orElseThrow(() -> new ResourceNotFoundException("No such account with client id " + clientId));

        return account.getCurrentAuthenticationHash().getPasswordPartCharactersPosition();
    }

    @Override
    public Account loadUserByUsername(String clientId) throws UsernameNotFoundException {
        return accountRepository.findById(clientId).
                orElseThrow(() -> new UsernameNotFoundException(String.format("Account %s not found", clientId)));
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
}
