package pl.edu.pw.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.edu.pw.domain.Account;
import pl.edu.pw.dto.AccountCurrencyBalance;
import pl.edu.pw.dto.AccountDTO;
import pl.edu.pw.dto.AddCurrency;
import pl.edu.pw.dto.AddFavoriteReceiver;
import pl.edu.pw.dto.FavoriteReceiverDTO;
import pl.edu.pw.service.account.AccountService;

import java.util.List;

@RestController
@RequestMapping(path = "/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/profile")
    public ResponseEntity<AccountDTO> getAccountData(@AuthenticationPrincipal Account account) {
        return ResponseEntity.ok(accountService.getAccountData(account));
    }

    @PutMapping("/currency")
    public ResponseEntity<Void> addCurrency(@AuthenticationPrincipal Account account, @RequestBody AddCurrency addCurrency) {
        accountService.addCurrencyBalance(account, addCurrency);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/currency/all")
    public ResponseEntity<List<AccountCurrencyBalance>> getAccountTotalBalance(@AuthenticationPrincipal Account account) {
        List<AccountCurrencyBalance> accountTotalBalance = accountService.getAccountCurrenciesBalance(account);
        return new ResponseEntity<>(accountTotalBalance, HttpStatus.OK);
    }

    @GetMapping("/receiver/all")
    public ResponseEntity<List<FavoriteReceiverDTO>> getFavoriteReceivers(@AuthenticationPrincipal Account account) {
        return ResponseEntity.ok(accountService.getAllFavoriteReceivers(account));
    }

    @PostMapping("/receiver")
    public ResponseEntity<FavoriteReceiverDTO> addFavoriteReceiver(@AuthenticationPrincipal Account account,
                                                    @RequestBody AddFavoriteReceiver addFavoriteReceiver) {
        FavoriteReceiverDTO createdFavoriteReceiver = accountService.addFavoriteReceiver(account, addFavoriteReceiver);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFavoriteReceiver);
    }
}
