package service;

import dto.CurrencyExchangeRequest;
import org.springframework.stereotype.Service;
import pl.edu.pw.domain.Account;
import pl.edu.pw.domain.Currency;
import pl.edu.pw.domain.SubAccount;
import pl.edu.pw.util.CurrentUserUtil;

import java.util.Map;
import java.util.Set;

@Service
public class CurrencyExchangeServiceImpl implements CurrencyExchangeService {

    @Override
    public void exchangeCurrency(CurrencyExchangeRequest request) {
        Account user = CurrentUserUtil.getCurrentUser();
        Map<Currency,SubAccount> subAccounts = user.getSubAccounts();
        System.out.println("Key : "+subAccounts.get("USD").getCurrency());

//        check if user has enough given currency to sell
//        if(subAccounts.)


    }
}
