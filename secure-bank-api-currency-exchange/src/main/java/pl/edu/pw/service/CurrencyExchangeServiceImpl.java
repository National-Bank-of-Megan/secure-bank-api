package pl.edu.pw.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.edu.pw.domain.Account;
import pl.edu.pw.domain.Currency;
import pl.edu.pw.domain.CurrencyExchange;
import pl.edu.pw.domain.SubAccount;
import pl.edu.pw.domain.SubAccountId;
import pl.edu.pw.dto.CurrencyExchangeDto;
import pl.edu.pw.dto.CurrencyExchangeRequest;
import pl.edu.pw.exception.ExternalApiException;
import pl.edu.pw.exception.InvalidCurrencyException;
import pl.edu.pw.exception.SubAccountBalanceException;
import pl.edu.pw.exception.SubAccountNotFoundException;
import pl.edu.pw.repository.CurrencyExchangeRepository;
import pl.edu.pw.repository.SubAccountRepository;
import pl.edu.pw.util.CurrentUserUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CurrencyExchangeServiceImpl implements CurrencyExchangeService {

    private static final Logger log = LoggerFactory.getLogger(CurrencyExchangeServiceImpl.class);
    private final SubAccountRepository subAccountRepository;
    private final CurrencyExchangeRepository currencyExchangeRepository;

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
        BigDecimal balance = sellingSubAccount.getBalance();
        if (balance.compareTo(request.getSold()) < 0)
            throw new SubAccountBalanceException("Not enough money on the " + request.getCurrencySold() + " sub account");
        sellingSubAccount.setBalance(balance.subtract(request.getSold()));

        BigDecimal bought;
        try {
            bought = ExternalCurrencyApiUtil.exchangeCurrency(currencySold, request.getSold(), request.getExchangeTime(), currencyBought);
        } catch (IOException e) {
            throw new ExternalApiException();
        }

////        Add bought money to another sub account
        SubAccount buyingSubAccount = subAccounts.get(currencyBought);
        buyingSubAccount.addToBalance(bought);
        subAccountRepository.save(buyingSubAccount);
        subAccountRepository.save(sellingSubAccount);

//        add to exchange to history
        CurrencyExchange exchange = new CurrencyExchange(
                request.getExchangeTime(),
                currencyBought,
                bought,
                currencySold,
                request.getSold(),
                user
        );

        currencyExchangeRepository.save(exchange);
    }

    @Override
    public List<CurrencyExchangeDto> getUserExchanges() {
        Account user = CurrentUserUtil.getCurrentUser();
        return user.getExchanges().stream().map(CurrencyExchangeServiceImpl::map).collect(Collectors.toList());
    }

    private SubAccount createNewSubAccount(Account account, Currency currency) {
        SubAccount newSubAccount = new SubAccount(new SubAccountId(account, currency), BigDecimal.ZERO);
        subAccountRepository.save(newSubAccount);
        return newSubAccount;
    }

    private static CurrencyExchangeDto map(CurrencyExchange currencyExchange) {
        return new CurrencyExchangeDto(
                currencyExchange.getOrderedOn(),
                currencyExchange.getCurrencyBought().toString(),
                currencyExchange.getAmountBought(),
                currencyExchange.getCurrencySold().toString(),
                currencyExchange.getAmountSold()
        );
    }
}
