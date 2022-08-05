package pl.edu.pw.logic.integration;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.edu.pw.logic.model.PendingTransfer;

@RequiredArgsConstructor
@Component
public class MessagesProcessingImpl implements MessagesProcessing {
    @Override
    public void processPendingTransfer(PendingTransfer pendingTransfer) {

    }
}
