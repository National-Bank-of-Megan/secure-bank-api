package pl.edu.pw.service;

import pl.edu.pw.domain.Transfer;
import pl.edu.pw.dto.TransferCreate;
import pl.edu.pw.dto.TransferDTO;
import pl.edu.pw.dto.TransferUpdate;
import pl.edu.pw.logic.model.PendingTransfer;
import pl.edu.pw.model.MoneyBalanceOperation;

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
