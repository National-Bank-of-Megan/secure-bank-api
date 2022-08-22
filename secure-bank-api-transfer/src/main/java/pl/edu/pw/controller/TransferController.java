package pl.edu.pw.controller;

import lombok.RequiredArgsConstructor;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.edu.pw.domain.Account;
import pl.edu.pw.dto.HistoryTransferDTO;
import pl.edu.pw.model.MoneyBalanceOperation;
import pl.edu.pw.dto.TransferCreate;
import pl.edu.pw.dto.TransferDTO;
import pl.edu.pw.dto.TransferUpdate;
import pl.edu.pw.service.TransferService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/transfer")
@RequiredArgsConstructor
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

//    @GetMapping("/{transferId}")
//    public ResponseEntity<TransferDTO> getTransfer(@AuthenticationPrincipal Account account, @PathVariable Long transferId) {
//        return ResponseEntity.ok(transferService.getTransfer(transferId, account.getClientId()));
//    }

//    @PutMapping
//    public ResponseEntity<Void> updateTransfer(@RequestBody TransferUpdate transferUpdate) {
//        transferService.update(transferUpdate);
//        return ResponseEntity.noContent().build();
//    }
//
//    @DeleteMapping("/{transferId}")
//    public ResponseEntity<Void> deleteTransfer(@PathVariable Long transferId) {
//        transferService.delete(transferId);
//        return ResponseEntity.accepted().build();
//    }
}
