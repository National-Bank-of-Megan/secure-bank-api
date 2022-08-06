package pl.edu.pw.logic.integration;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.edu.pw.logic.model.PendingTransfer;
import pl.edu.pw.service.TransferService;

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
