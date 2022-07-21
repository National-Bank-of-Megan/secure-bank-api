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
import pl.edu.pw.exception.SubAccountNotFoundException;
import pl.edu.pw.repository.SubAccountRepository;
import pl.edu.pw.security.filter.WebAuthenticationFilter;
import pl.edu.pw.util.CurrentUserUtil;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CurrencyExchangeServiceImpl implements CurrencyExchangeService {

    private static final Logger log = LoggerFactory.getLogger(CurrencyExchangeServiceImpl.class);
    private final SubAccountRepository subAccountRepository;

    @Override
    public void exchangeCurrency(CurrencyExchangeRequest request) {
        Account user = CurrentUserUtil.getCurrentUser();
        log.info("Current client: ", user.getClientId());

//        todo delete, only temporary helper
        List<SubAccount> subAccounts = user.getSubAccounts();
        subAccounts.forEach((a) -> {
            log.info("Subaccount: ", a.getId().getCurrency());
        });
//        =================================================

        SubAccount soldSubAccount =
                subAccountRepository.findById(new SubAccountId(user, Currency.valueOf(request.getCurrencySold()))).orElseThrow(
                        () -> new SubAccountNotFoundException("You are trying to sell currency form sub account that does not exists")
                );

        SubAccount boughtSubAccount = subAccountRepository.findById(new SubAccountId(user, Currency.valueOf(request.getCurrencyBought()))).orElseGet(
                ()-> createNewSubAccount(user, Currency.valueOf(request.getCurrencyBought()))
                );


//        check if user has enough given currency to sell
//        if(subAccounts.)


    }

    private SubAccount createNewSubAccount(Account account, Currency currency) {
        SubAccount newSubAccount = new SubAccount(new SubAccountId(account, currency), 0.00);
        subAccountRepository.save(newSubAccount);
        return newSubAccount;
    }
}
