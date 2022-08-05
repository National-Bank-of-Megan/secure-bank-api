package pl.edu.pw.logic.producer;

import pl.edu.pw.logic.model.PendingTransfer;

public interface TransfersSender {
    void sendPendingTransfer(Long transferId, PendingTransfer pendingTransfer);
}
