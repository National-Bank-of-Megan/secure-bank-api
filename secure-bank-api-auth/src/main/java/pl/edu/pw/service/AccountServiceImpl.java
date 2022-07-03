package pl.edu.pw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.edu.pw.auth.logic.PasswordHashesGenerator;
import pl.edu.pw.dto.AccountRegistration;
import pl.edu.pw.dto.PartPasswordHash;
import pl.edu.pw.repository.AccountRepository;
import pl.edu.pw.user.Account;
import pl.edu.pw.user.AccountHash;

import javax.transaction.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@Transactional
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private static final String NO_SUCH_ACCOUNT_MESSAGE = "No such account";

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

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
        return accountRepository.findByAccountNumber(accountNumber).orElse(null);
    }

    @Override
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

}
