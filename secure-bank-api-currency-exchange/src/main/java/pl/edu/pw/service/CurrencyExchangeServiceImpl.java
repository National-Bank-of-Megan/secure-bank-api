package pl.edu.pw.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pw.domain.Currency;
import pl.edu.pw.domain.SubAccountId;
import pl.edu.pw.dto.CurrencyExchangeRequest;
import org.springframework.stereotype.Service;
import pl.edu.pw.domain.Account;
import pl.edu.pw.domain.SubAccount;
import pl.edu.pw.exception.ExternalApiException;
import pl.edu.pw.exception.InvalidCurrencyException;
import pl.edu.pw.exception.SubAccountBalanceException;
import pl.edu.pw.exception.SubAccountNotFoundException;
import pl.edu.pw.repository.SubAccountRepository;
import pl.edu.pw.util.CurrentUserUtil;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CurrencyExchangeServiceImpl implements CurrencyExchangeService {

    private static final Logger log = LoggerFactory.getLogger(CurrencyExchangeServiceImpl.class);
    private final SubAccountRepository subAccountRepository;

    @Override
    public void exchangeCurrency(CurrencyExchangeRequest request) {
        Account user = CurrentUserUtil.getCurrentUser();
        Map<Currency, SubAccount> subAccounts = user.getSubAccounts();
        Currency currencySold;
        Currency currencyBought;
        try {
            currencyBought = Currency.valueOf(request.getCurrencyBought());
            currencySold = Currency.valueOf(request.getCurrencySold());
        } catch (Exception e) {
            throw new InvalidCurrencyException("Invalid currency provided");
        }

//        check whether sub accounts exist
        SubAccount soldSubAccount =
                subAccountRepository.findById(new SubAccountId(user, currencySold)).orElseThrow(
                        () -> new SubAccountNotFoundException("You are trying to sell currency from sub account that does not exists")
                );

        SubAccount boughtSubAccount = subAccountRepository.findById(new SubAccountId(user, currencyBought)).orElseGet(
                () -> createNewSubAccount(user, Currency.valueOf(request.getCurrencyBought()))
        );

        //        check if there is enough money to be sold and delete from balance
        SubAccount sellingSubAccount = subAccounts.get(currencySold);
        double balance = sellingSubAccount.getBalance();
        if (balance < request.getSold())
            throw new SubAccountBalanceException("Not enough money on the " + request.getCurrencySold() + " sub account");
        sellingSubAccount.setBalance(balance - request.getSold());

        double bought;
        try {
            bought = ExternalCurrencyApiUtil.exchangeCurrency(currencySold,request.getSold(),request.getExchangeTime(),currencyBought);
        } catch (IOException e) {
            throw new ExternalApiException();
        }

////        Add bought money to another sub account
        SubAccount buyingSubAccount = subAccounts.get(Currency.valueOf(request.getCurrencyBought()));
        buyingSubAccount.addToBalance(bought);
        subAccountRepository.save(buyingSubAccount);
        subAccountRepository.save(sellingSubAccount);

//        add to exchange to history








    }

    private SubAccount createNewSubAccount(Account account, Currency currency) {
        SubAccount newSubAccount = new SubAccount(new SubAccountId(account, currency), 0.00);
        subAccountRepository.save(newSubAccount);
        return newSubAccount;
    }
}
