package pl.edu.pw.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.edu.pw.domain.Account;
import pl.edu.pw.dto.TransferCreate;
import pl.edu.pw.dto.TransferDTO;
import pl.edu.pw.dto.TransferUpdate;
import pl.edu.pw.service.TransferService;

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

    @GetMapping("/{transferId}")
    public ResponseEntity<TransferDTO> getTransfer(@PathVariable Long transferId) {
        return ResponseEntity.ok(transferService.getTransfer(transferId));
    }

    @PostMapping
    public ResponseEntity<Void> makeTransfer(@RequestBody TransferCreate transferCreate) {
        transferService.create(transferCreate);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Void> updateTransfer(@RequestBody TransferUpdate transferUpdate) {
        transferService.update(transferUpdate);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{transferId}")
    public ResponseEntity<Void> deleteTransfer(@PathVariable Long transferId) {
        transferService.delete(transferId);
        return ResponseEntity.accepted().build();
    }
}
