package pl.edu.pw.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.edu.pw.domain.Account;
import pl.edu.pw.dto.AccountCurrencyBalance;
import pl.edu.pw.dto.AddCurrency;
import pl.edu.pw.service.account.AccountService;

import java.util.List;

@RestController
@RequestMapping(path = "/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

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
}
