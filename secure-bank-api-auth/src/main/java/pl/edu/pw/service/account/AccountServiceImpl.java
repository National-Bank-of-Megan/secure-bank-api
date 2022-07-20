package pl.edu.pw.service.account;

import pl.edu.pw.domain.Account;
import pl.edu.pw.domain.Currency;
import pl.edu.pw.domain.SubAccount;
import pl.edu.pw.dto.AccountCurrencyBalance;
import pl.edu.pw.dto.AddCurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AccountServiceImpl implements AccountService {

    @Override
    public void addCurrencyBalance(Account account, AddCurrency addCurrency) {
        Currency currency;
        if (addCurrency.getAmount() <= 0) {
            throw new IllegalArgumentException("Amount to add cannot be lower or equal to 0.");
        }
        try {
            currency = Currency.valueOf(addCurrency.getCurrency());
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("Currency " + addCurrency.getCurrency() + " not found.");
        }
        account.addCurrencyBalance(currency, addCurrency.getAmount());
    }

    @Override
    public List<AccountCurrencyBalance> getAccountCurrenciesBalance(Account account) {
        List<AccountCurrencyBalance> accountCurrencyBalanceList = new ArrayList<>();
        Map<Currency, SubAccount> accountSubAccounts = account.getSubAccounts();
        for (Currency currency : accountSubAccounts.keySet()) {
            String currencyName = currency.name();
            double balance = accountSubAccounts.get(currency).getBalance();
            accountCurrencyBalanceList.add(new AccountCurrencyBalance(currencyName, balance));
        }
        return accountCurrencyBalanceList;
    }
}
