package pl.edu.pw.transfer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.edu.pw.core.domain.Account;
import pl.edu.pw.transfer.dto.TransferCreate;
import pl.edu.pw.core.dto.TransferDTO;
import pl.edu.pw.core.model.MoneyBalanceOperation;
import pl.edu.pw.transfer.service.TransferService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/transfer")
@RequiredArgsConstructor
@PreAuthorize("@accountSecurity.doesUserHaveTransferAuthority()")
public class TransferController {

    private final TransferService transferService;

    @GetMapping
    public ResponseEntity<List<TransferDTO>> getAll(@AuthenticationPrincipal Account account) {
        return ResponseEntity.ok(transferService.getAll(account.getClientId()));
    }

    @GetMapping("/recentActivity")
    public ResponseEntity<List<MoneyBalanceOperation>> getRecentActivity(@AuthenticationPrincipal Account account) {
        return ResponseEntity.ok(transferService.getRecentActivity(account.getClientId()));
    }

    @PostMapping
    public ResponseEntity<Void> makeTransfer(@AuthenticationPrincipal Account account,
                                             @RequestBody @Valid TransferCreate transferCreate) {
        transferService.create(transferCreate, account.getClientId());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
