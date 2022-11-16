package pl.edu.pw.service.account;

import pl.edu.pw.domain.Account;
import pl.edu.pw.dto.*;

import java.util.List;

public interface AccountService {
    void addCurrencyBalance(Account account, AddCurrencyBalance addCurrencyBalance);

    List<AccountCurrencyBalance> getAccountCurrenciesBalance(Account account);

    List<FavoriteReceiverDTO> getAllFavoriteReceivers(Account account);

    FavoriteReceiverDTO addFavoriteReceiver(Account account, AddFavoriteReceiver addFavoriteReceiver);

    AccountDTO getAccountData(Account account);

    void resetLoginAttempts(Account account);

    void updateLoginAttempts(Account account, long attempts);

    void lockAccount(Account account);

    void unlockAccount(Account account);

    void changePassword(Account account, ChangePassword changePassword);
}
