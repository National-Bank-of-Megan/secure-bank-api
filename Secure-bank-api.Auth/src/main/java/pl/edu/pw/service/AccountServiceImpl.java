package pl.edu.pw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.edu.pw.dto.AccountRegistration;
import pl.edu.pw.repository.AccountRepository;
import pl.edu.pw.user.Account;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService, UserDetailsService {
    private final AccountRepository accountRepository;

    @Override
    public Account registerAccount(AccountRegistration registerData) {
        return null;
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
}
