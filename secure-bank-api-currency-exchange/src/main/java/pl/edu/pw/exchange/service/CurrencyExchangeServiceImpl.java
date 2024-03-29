package pl.edu.pw.exchange.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.edu.pw.core.domain.*;
import pl.edu.pw.core.dto.CurrencyExchangeDto;
import pl.edu.pw.exchange.dto.CurrencyExchangeRequest;
import pl.edu.pw.auth.exception.ExternalApiException;
import pl.edu.pw.core.exception.InvalidCurrencyException;
import pl.edu.pw.core.exception.SubAccountBalanceException;
import pl.edu.pw.core.exception.SubAccountNotFoundException;
import pl.edu.pw.core.repository.CurrencyExchangeRepository;
import pl.edu.pw.core.repository.SubAccountRepository;
import pl.edu.pw.core.util.CurrentUserUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CurrencyExchangeServiceImpl implements CurrencyExchangeService {

    private static final Logger log = LoggerFactory.getLogger(CurrencyExchangeServiceImpl.class);
    private final SubAccountRepository subAccountRepository;
    private final CurrencyExchangeRepository currencyExchangeRepository;

    private static CurrencyExchangeDto map(CurrencyExchange currencyExchange) {
        return new CurrencyExchangeDto(
                currencyExchange.getId(),
                currencyExchange.getOrderedOn(),
                currencyExchange.getCurrencyBought().toString(),
                currencyExchange.getAmountBought(),
                currencyExchange.getCurrencySold().toString(),
                currencyExchange.getAmountSold()
        );
    }

    @Override
    public void exchangeCurrency(CurrencyExchangeRequest currencyExchangeRequest) {
        Account user = CurrentUserUtil.getCurrentUser();
        Map<Currency, SubAccount> subAccounts = user.getSubAccounts();
        Currency currencySold;
        Currency currencyBought;
        try {
            currencyBought = Currency.valueOf(currencyExchangeRequest.getCurrencyBought());
            currencySold = Currency.valueOf(currencyExchangeRequest.getCurrencySold());
        } catch (Exception e) {
            throw new InvalidCurrencyException("Invalid currency provided");
        }

//        check whether sub accounts exist
        SubAccount soldSubAccount =
                subAccountRepository.findById(new SubAccountId(user, currencySold)).orElseThrow(
                        () -> new SubAccountNotFoundException("You are trying to sell currency from sub account that does not exists")
                );

        SubAccount boughtSubAccount = subAccountRepository.findById(new SubAccountId(user, currencyBought)).orElseGet(
                () -> createNewSubAccount(user, Currency.valueOf(currencyExchangeRequest.getCurrencyBought()))
        );

        //        check if there is enough money to be sold and delete from balance
        SubAccount sellingSubAccount = subAccounts.get(currencySold);
        BigDecimal balance = sellingSubAccount.getBalance();
        if (balance.compareTo(currencyExchangeRequest.getSold()) < 0)
            throw new SubAccountBalanceException("Not enough money on the " + currencyExchangeRequest.getCurrencySold() + " sub account");
        sellingSubAccount.setBalance(balance.subtract(currencyExchangeRequest.getSold()));

        BigDecimal bought;
        try {
            bought = ExternalCurrencyApiUtil.exchangeCurrency(currencySold, currencyExchangeRequest.getSold(), LocalDateTime.now(), currencyBought);
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
                LocalDateTime.now(),
                currencyBought,
                bought,
                currencySold,
                currencyExchangeRequest.getSold(),
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
}
