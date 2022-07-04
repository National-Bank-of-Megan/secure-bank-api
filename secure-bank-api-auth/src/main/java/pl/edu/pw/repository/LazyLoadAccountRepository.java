package pl.edu.pw.repository;

import pl.edu.pw.user.Account;

public interface LazyLoadAccountRepository {
    Account getAccountLazy(String accountNumber);
}
