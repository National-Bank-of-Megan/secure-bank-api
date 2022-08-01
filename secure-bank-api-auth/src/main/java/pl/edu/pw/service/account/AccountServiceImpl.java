package pl.edu.pw.service.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.pw.domain.*;
import pl.edu.pw.dto.AccountCurrencyBalance;
import pl.edu.pw.dto.AddCurrency;
import pl.edu.pw.dto.AddFavoriteReceiver;
import pl.edu.pw.dto.FavoriteReceiverDTO;
import pl.edu.pw.exception.InvalidCurrencyException;
import pl.edu.pw.exception.SubAccountNotFoundException;
import pl.edu.pw.repository.AccountRepository;
import pl.edu.pw.repository.FavoriteReceiverRepository;
import pl.edu.pw.repository.SubAccountRepository;
import pl.edu.pw.util.CurrentUserUtil;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final SubAccountRepository subAccountRepository;
    private final FavoriteReceiverRepository favoriteReceiverRepository;

    @Override
    public void addCurrencyBalance(Account account, AddCurrency addCurrency) {
        Currency currency;
        if (addCurrency.getAmount() <= 0) {
            throw new IllegalArgumentException("Amount to add cannot be lower or equal to 0.");
        }
        try {
            currency = Currency.valueOf(addCurrency.getCurrency());
        } catch (NullPointerException e) {
            throw new InvalidCurrencyException("Currency " + addCurrency.getCurrency() + " not found.");
        }
//        account.addCurrencyBalance(currency, addCurrency.getAmount());
//        todo catch exception when subaccount not found
        SubAccount subAccount = subAccountRepository.findById(new SubAccountId(CurrentUserUtil.getCurrentUser(),currency)).orElseThrow(
                ()-> new SubAccountNotFoundException("Subaccount not found. Something wrong with database")
        );
        subAccount.addToBalance(addCurrency.getAmount());
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

    @Override
    public List<FavoriteReceiverDTO> getAllFavoriteReceivers(Account account) {
        return account.getFavoriteReceivers().stream().map(AccountMapper::map).toList();
    }

    @Override
    public FavoriteReceiverDTO addFavoriteReceiver(Account loggedAccount, AddFavoriteReceiver addFavoriteReceiver) {
        Account account = accountRepository.findById(loggedAccount.getClientId()).orElseThrow();
        FavoriteReceiver favoriteReceiver = AccountMapper.map(addFavoriteReceiver);
        account.addFavoriteReceiver(favoriteReceiver);
        return AccountMapper.map(favoriteReceiverRepository.save(favoriteReceiver));
    }

    public static class AccountMapper {
        public static FavoriteReceiverDTO map(FavoriteReceiver favoriteReceiver) {
            return new FavoriteReceiverDTO(
                    favoriteReceiver.getId(),
                    favoriteReceiver.getName(),
                    favoriteReceiver.getAccountNumber()
            );
        }

        public static FavoriteReceiver map(AddFavoriteReceiver addFavoriteReceiver) {
            return FavoriteReceiver.builder()
                    .name(addFavoriteReceiver.getName())
                    .accountNumber(addFavoriteReceiver.getAccountNumber())
                    .build();
        }
    }
}
