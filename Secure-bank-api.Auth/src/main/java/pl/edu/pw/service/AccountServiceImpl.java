package pl.edu.pw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.edu.pw.dto.AccountRegistration;
import pl.edu.pw.repository.AccountRepository;
import pl.edu.pw.user.Account;

import javax.transaction.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

import static pl.edu.pw.service.AccountServiceImpl.AccountMapper.map;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService, UserDetailsService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void registerAccount(AccountRegistration registerData) {
        registerData.setPassword(passwordEncoder.encode(registerData.getPassword()));
        Set<String> accountsNumbers = accountRepository.findAll().stream().map(Account::getAccountNumber).collect(Collectors.toSet());
        Account accountToRegister = map(registerData, accountsNumbers);
        accountRepository.save(accountToRegister);
    }

    @Override
    public Account getAccount(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber).orElse(null);
    }

    @Override
    public UserDetails loadUserByUsername(String accountNumber) throws UsernameNotFoundException {
        return accountRepository.findByAccountNumber(accountNumber).
                orElseThrow(() -> new UsernameNotFoundException(String.format("Account number %s not found", accountNumber)));
    }

    public static class AccountMapper {
        public static Account map(AccountRegistration registerData, Set<String> existingAccountsNumbers) {
            return new Account(
                existingAccountsNumbers,
                registerData.getPassword()
            );
        }
    }
}
