package pl.edu.pw.repository;

import pl.edu.pw.domain.Account;

public interface LazyLoadAccountRepository {
    Account getAccountLazy(String accountNumber);
}
