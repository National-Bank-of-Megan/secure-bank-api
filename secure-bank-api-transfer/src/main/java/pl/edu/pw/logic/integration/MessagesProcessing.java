package pl.edu.pw.logic.integration;

import pl.edu.pw.logic.model.PendingTransfer;

public interface MessagesProcessing {
    void processPendingTransfer(PendingTransfer pendingTransfer);
}
