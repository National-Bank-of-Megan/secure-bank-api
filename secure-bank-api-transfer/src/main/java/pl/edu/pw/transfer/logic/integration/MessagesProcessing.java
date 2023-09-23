package pl.edu.pw.transfer.logic.integration;

import pl.edu.pw.transfer.logic.model.PendingTransfer;

public interface MessagesProcessing {
    void processPendingTransfer(PendingTransfer pendingTransfer);
}
