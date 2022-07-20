package pl.edu.pw.service.account;

import pl.edu.pw.domain.Account;
import pl.edu.pw.dto.AccountCurrencyBalance;
import pl.edu.pw.dto.AddCurrency;

import java.util.List;

public interface AccountService {
    void addCurrencyBalance(Account account, AddCurrency addCurrency);

    List<AccountCurrencyBalance> getAccountCurrenciesBalance(Account account);
}
