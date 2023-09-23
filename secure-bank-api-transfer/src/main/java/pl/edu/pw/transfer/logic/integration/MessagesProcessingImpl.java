package pl.edu.pw.transfer.logic.integration;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.edu.pw.transfer.logic.model.PendingTransfer;
import pl.edu.pw.transfer.service.TransferService;

@Component
@RequiredArgsConstructor
public class MessagesProcessingImpl implements MessagesProcessing {

    private final TransferService transferService;

    @Override
    public void processPendingTransfer(PendingTransfer pendingTransfer) {
        if (pendingTransfer != null) {
            transferService.finalizeTransfer(pendingTransfer);
        }
    }
}
