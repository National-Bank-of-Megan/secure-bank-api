package pl.edu.pw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pw.domain.Account;
import pl.edu.pw.domain.Currency;
import pl.edu.pw.domain.Status;
import pl.edu.pw.domain.Transfer;
import pl.edu.pw.dto.TransferCreate;
import pl.edu.pw.dto.TransferDTO;
import pl.edu.pw.dto.TransferUpdate;
import pl.edu.pw.repository.AccountRepository;
import pl.edu.pw.repository.TransferRepository;

import java.util.Date;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final TransferRepository transferRepository;
    private final AccountRepository accountRepository;

    @Override
    public List<TransferDTO> getAll(String clientId) {
        return transferRepository.findAll().stream().filter(transfer -> isClientTransfer(transfer, clientId)).map(TransferMapper::map).toList();
    }

    private boolean isClientTransfer(Transfer transfer, String clientId) {
        return transfer.getReceiver().getClientId().equals(clientId) || transfer.getSender().getClientId().equals(clientId);
    }

    @Override
    public TransferDTO getTransfer(Long transferId) {
        return transferRepository.findById(transferId).map(TransferMapper::map).orElseThrow();
    }

    @Override
    public Transfer create(TransferCreate transferCreate) {
        Account senderAccount = accountRepository.findById(transferCreate.getSenderId()).orElseThrow();
        Account receiverAccount = accountRepository.findById(transferCreate.getReceiverId()).orElseThrow();
        Transfer transferToSave = TransferMapper.map(transferCreate, senderAccount, receiverAccount);
        return transferRepository.save(transferToSave);
    }

    @Override
    public void delete(Long transferId) {
        transferRepository.deleteById(transferId);
    }

    @Override
    public void update(TransferUpdate transferUpdate) {

    }

    public static class TransferMapper {
        public static TransferDTO map(Transfer transfer) {
            return TransferDTO.builder()
                    .receiver("TODO")
                    .title(transfer.getTitle())
                    .amount(transfer.getAmount())
                    .currency(transfer.getCurrency().name())
                    .requestDate(transfer.getRequestDate())
                    .doneDate(transfer.getDoneDate())
                    .status(transfer.getStatus().name())
                    .balanceAfter(123).build();
        }

        public static Transfer map(TransferCreate transferCreate, Account sender, Account receiver) {
            return Transfer.builder()
                    .requestDate(new Date())
                    .title(transferCreate.getTitle())
                    .status(Status.PENDING)
                    .sender(sender)
                    .receiver(receiver)
                    .amount(transferCreate.getAmount())
                    .currency(Currency.valueOf(transferCreate.getCurrency())).build();
        }
    }
}
