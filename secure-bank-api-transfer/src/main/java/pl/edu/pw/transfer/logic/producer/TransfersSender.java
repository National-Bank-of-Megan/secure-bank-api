package pl.edu.pw.transfer.logic.producer;

import pl.edu.pw.transfer.logic.model.PendingTransfer;

public interface TransfersSender {
    void sendPendingTransfer(Long transferId, PendingTransfer pendingTransfer);
}
