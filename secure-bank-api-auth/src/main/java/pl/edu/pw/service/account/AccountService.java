package pl.edu.pw.service.account;

import org.springframework.http.ResponseEntity;
import pl.edu.pw.domain.Account;
import pl.edu.pw.dto.AccountCurrencyBalance;
import pl.edu.pw.dto.AccountDTO;
import pl.edu.pw.dto.AddCurrency;
import pl.edu.pw.dto.AddFavoriteReceiver;
import pl.edu.pw.dto.ChangePassword;
import pl.edu.pw.dto.FavoriteReceiverDTO;

import java.util.List;

public interface AccountService {
    void addCurrencyBalance(Account account, AddCurrency addCurrency);
    List<AccountCurrencyBalance> getAccountCurrenciesBalance(Account account);
    List<FavoriteReceiverDTO> getAllFavoriteReceivers(Account account);

    FavoriteReceiverDTO addFavoriteReceiver(Account account, AddFavoriteReceiver addFavoriteReceiver);

    AccountDTO getAccountData(Account account);

    void changePassword(Account account, ChangePassword changePassword);
}
