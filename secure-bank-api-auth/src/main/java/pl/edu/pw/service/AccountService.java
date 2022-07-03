package pl.edu.pw.service;

import pl.edu.pw.dto.AccountRegistration;
import pl.edu.pw.user.Account;

public interface AccountService {
    void registerAccount(AccountRegistration registerData);
    Account getAccount(String accountNumber);
    String getLoginCombination(String username);
    Account getAccountLazy(String accountNumber);
}
