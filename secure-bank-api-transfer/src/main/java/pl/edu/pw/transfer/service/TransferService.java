package pl.edu.pw.transfer.service;

import pl.edu.pw.core.domain.Transfer;
import pl.edu.pw.transfer.dto.TransferCreate;
import pl.edu.pw.core.dto.TransferDTO;
import pl.edu.pw.transfer.dto.TransferUpdate;
import pl.edu.pw.transfer.logic.model.PendingTransfer;
import pl.edu.pw.core.model.MoneyBalanceOperation;

import java.util.List;

public interface TransferService {

    List<TransferDTO> getAll(String clientId);

    TransferDTO getTransfer(Long transferId, String clientId);

    Transfer create(TransferCreate transferCreate, String senderId);

    void delete(Long transferId);

    void update(TransferUpdate transferUpdate);

    void finalizeTransfer(PendingTransfer pendingTransfer);

    List<MoneyBalanceOperation> getRecentActivity(String clientId);
}
