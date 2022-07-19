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
        return new ResponseEntity<>(transferService.getAll(account.getClientId()), HttpStatus.OK);
    }

    @GetMapping("/{transferId}")
    public ResponseEntity<TransferDTO> getTransfer(@PathVariable Long transferId) {
        return new ResponseEntity<>(transferService.getTransfer(transferId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Void> createTransfer(@RequestBody TransferCreate transferCreate) {
        transferService.create(transferCreate);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Void> updateTransfer(@RequestBody TransferUpdate transferUpdate) {
        transferService.update(transferUpdate);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{transferId}")
    public ResponseEntity<Void> deleteTransfer(@PathVariable Long transferId) {
        transferService.delete(transferId);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
