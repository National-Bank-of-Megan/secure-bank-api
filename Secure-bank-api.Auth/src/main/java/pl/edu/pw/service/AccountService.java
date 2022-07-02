package pl.edu.pw.service;

import org.springframework.stereotype.Service;
import pl.edu.pw.dto.AccountRegistration;
import pl.edu.pw.user.Account;

import javax.transaction.Transactional;

public interface AccountService {
    void registerAccount(AccountRegistration registerData);
    Account getAccount(String accountNumber);
}
