package pl.edu.pw.service;

import pl.edu.pw.domain.Transfer;
import pl.edu.pw.dto.TransferCreate;
import pl.edu.pw.dto.TransferDTO;
import pl.edu.pw.dto.TransferUpdate;

import java.util.List;

public interface TransferService {

    List<TransferDTO> getAll(String clientId);

    TransferDTO getTransfer(Long transferId);

    Transfer create(TransferCreate transferCreate);

    void delete(Long transferId);

    void update(TransferUpdate transferUpdate);
}
