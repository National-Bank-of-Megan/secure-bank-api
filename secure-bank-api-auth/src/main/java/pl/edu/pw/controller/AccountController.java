package pl.edu.pw.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pw.domain.Account;
import pl.edu.pw.dto.AccountCurrencyBalance;
import pl.edu.pw.dto.AccountDTO;
import pl.edu.pw.dto.AddCurrencyBalance;
import pl.edu.pw.dto.AddFavoriteReceiver;
import pl.edu.pw.dto.ChangePassword;
import pl.edu.pw.dto.DeviceDTO;
import pl.edu.pw.dto.FavoriteReceiverDTO;
import pl.edu.pw.service.account.AccountService;
import pl.edu.pw.service.devices.DevicesService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final DevicesService devicesService;

    @GetMapping("/profile")
    public ResponseEntity<AccountDTO> getAccountData(@AuthenticationPrincipal Account account) {
        return ResponseEntity.ok(accountService.getAccountData(account));
    }

    @GetMapping("/devices")
    public ResponseEntity<List<DeviceDTO>> getAccountDevices(@AuthenticationPrincipal Account account, HttpServletRequest request) {
        String deviceFingerprint = request.getHeader("Device-Fingerprint");
        if (deviceFingerprint == null) {
            throw new RuntimeException("Device-Fingerprint header is required to get trusted devices");
        }
        return ResponseEntity.ok(devicesService.getAccountVerifiedDevices(account.getClientId(), deviceFingerprint));
    }

    @PreAuthorize("@accountSecurity.isDeviceAttachedToAccount(#id, #account.getClientId())")
    @DeleteMapping("/devices/{id}")
    public ResponseEntity<Void> deleteDeviceFromTrustedDevices(@AuthenticationPrincipal Account account, @PathVariable Long id) {
        devicesService.deleteDeviceFromTrustedDevices(id, account.getClientId());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/currency")
    public ResponseEntity<Void> addCurrencyBalance(@AuthenticationPrincipal Account account,
                                                   @RequestBody @Valid AddCurrencyBalance addCurrencyBalance) {
        accountService.addCurrencyBalance(account, addCurrencyBalance);
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
                                                                   @RequestBody @Valid AddFavoriteReceiver addFavoriteReceiver) {
        FavoriteReceiverDTO createdFavoriteReceiver = accountService.addFavoriteReceiver(account, addFavoriteReceiver);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFavoriteReceiver);
    }

    @PutMapping("/changePassword")
    public ResponseEntity<Void> changePassword(@AuthenticationPrincipal Account account,
                                               @RequestBody @Valid ChangePassword changePassword) {
        accountService.changePassword(account, changePassword);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
