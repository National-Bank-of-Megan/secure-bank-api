package pl.edu.pw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pw.domain.Account;
import pl.edu.pw.domain.Currency;
import pl.edu.pw.domain.CurrencyExchange;
import pl.edu.pw.domain.Status;
import pl.edu.pw.domain.Transfer;
import pl.edu.pw.dto.CurrencyExchangeDto;
import pl.edu.pw.dto.HistoryTransferDTO;
import pl.edu.pw.dto.TransferType;
import pl.edu.pw.exception.ResourceNotFoundException;
import pl.edu.pw.model.MoneyBalanceOperation;
import pl.edu.pw.dto.TransferCreate;
import pl.edu.pw.dto.TransferDTO;
import pl.edu.pw.dto.TransferUpdate;
import pl.edu.pw.logic.model.PendingTransfer;
import pl.edu.pw.logic.producer.TransfersSender;
import pl.edu.pw.repository.AccountRepository;
import pl.edu.pw.repository.CurrencyExchangeRepository;
import pl.edu.pw.repository.TransferRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static pl.edu.pw.service.TransferServiceImpl.TransferMapper.mapToPending;

@Service
@Transactional
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final TransferRepository transferRepository;
    private final AccountRepository accountRepository;
    private final CurrencyExchangeRepository currencyExchangeRepository;
    private final TransfersSender transfersSender;

    @Override
    public List<TransferDTO> getAll(String clientId) {
        return transferRepository.findAllByReceiverClientIdOrSenderClientId(clientId, clientId).stream()
                                 .map(transfer -> TransferMapper.map(transfer, clientId)).toList();
    }

    @Override
    public TransferDTO getTransfer(Long transferId, String clientId) {
        return transferRepository.findById(transferId).map(transfer -> TransferMapper.map(transfer, clientId)).orElseThrow(() ->
                new ResourceNotFoundException("Transfer with " + transferId + " id was not found"));
    }

    @Override
    public Transfer create(TransferCreate transferCreate, String senderId) {
        if (transferCreate.getAmount().doubleValue() <= 0.0) {
            throw new IllegalArgumentException("Provide correct amount of money to transfer");
        }

        Currency currency;
        try {
            currency = Currency.valueOf(transferCreate.getCurrency().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown currency " + transferCreate.getCurrency());
        }

        Account senderAccount = accountRepository.findById(senderId).orElseThrow(() ->
                new ResourceNotFoundException("Account with " + senderId + " client id was not found"));
        Account receiverAccount = accountRepository.findByAccountNumber(transferCreate.getReceiverAccountNumber()).orElseThrow(() ->
                new ResourceNotFoundException("Account with " + transferCreate.getReceiverAccountNumber() + " account number was not found"));

        if (senderAccount.getSubAccounts().get(currency).getBalance().subtract(transferCreate.getAmount()).doubleValue() < 0.0) {
            throw new IllegalArgumentException("Too low " + transferCreate.getCurrency() + " balance on your account");
        }

        if (senderId.equals(receiverAccount.getClientId())) {
            throw new IllegalArgumentException("Cannot make transfer to yourself");
        }

        senderAccount.chargeCurrencyBalance(Currency.valueOf(transferCreate.getCurrency()), transferCreate.getAmount());
        Transfer transferToSave = TransferMapper.map(transferCreate, senderAccount, receiverAccount);
        Transfer savedTransfer = transferRepository.save(transferToSave);
        transfersSender.sendPendingTransfer(savedTransfer.getId(), mapToPending(savedTransfer));
        return savedTransfer;
    }

    @Override
    public void delete(Long transferId) {
        transferRepository.deleteById(transferId);
    }

    @Override
    public void update(TransferUpdate transferUpdate) {

    }

    @Override
    public void finalizeTransfer(PendingTransfer pendingTransfer) {
        Transfer foundPendingTransfer = transferRepository.findById(pendingTransfer.getId()).orElseThrow(() ->
                new ResourceNotFoundException("Transfer with " + pendingTransfer.getId() + " id was not found"));
        Account receiver = foundPendingTransfer.getReceiver();
        receiver.addCurrencyBalance(pendingTransfer.getCurrency(), pendingTransfer.getAmount());
        foundPendingTransfer.setDoneDate(LocalDateTime.now());
        foundPendingTransfer.setStatus(Status.DONE);
    }

    @Override
    public List<MoneyBalanceOperation> getRecentActivity(String clientId) {
        List<CurrencyExchangeDto> currencyExchangeDtoList = currencyExchangeRepository
                .findTop5ByAccountClientIdOrderByOrderedOnDesc(clientId).stream().map(this::map).toList();
        List<TransferDTO> transferDTOList = transferRepository
                .findTop5ByReceiverClientIdOrSenderClientIdOrderByRequestDateDesc(clientId, clientId).stream()
                .map(transfer -> TransferMapper.map(transfer, clientId)).toList();

        List<MoneyBalanceOperation> recentActivityList = new ArrayList<>();
        recentActivityList.addAll(currencyExchangeDtoList);
        recentActivityList.addAll(transferDTOList);
        recentActivityList.sort(Collections.reverseOrder());
        final int numberOfRecentActivities = 4;

        return recentActivityList.stream().limit(numberOfRecentActivities).toList();
    }

    private CurrencyExchangeDto map(CurrencyExchange currencyExchange) {
        return new CurrencyExchangeDto(
                currencyExchange.getOrderedOn(),
                currencyExchange.getCurrencyBought().toString(),
                currencyExchange.getAmountBought(),
                currencyExchange.getCurrencySold().toString(),
                currencyExchange.getAmountSold()
        );
    }

    public static class TransferMapper {
        public static TransferDTO map(Transfer transfer, String clientId) {
            TransferType transferType = null;
            if (transfer.getReceiver().getClientId().equals(clientId)) {
                transferType = TransferType.RECEIVED;
            } else if (transfer.getSender().getClientId().equals(clientId)) {
                transferType = TransferType.SENT;
            }

            return TransferDTO.builder()
                    .transferType(transferType)
                    .sender(transfer.getSender().getAccountDetails().getFirstName()
                            + " " + transfer.getSender().getAccountDetails().getLastName())
                    .receiver(transfer.getReceiver().getAccountDetails().getFirstName()
                              + " " + transfer.getReceiver().getAccountDetails().getLastName())
                    .title(transfer.getTitle())
                    .amount(transfer.getAmount())
                    .currency(transfer.getCurrency().name())
                    .requestDate(transfer.getRequestDate())
                    .doneDate(transfer.getDoneDate())
                    .status(transfer.getStatus().name()).build();
        }

        public static Transfer map(TransferCreate transferCreate, Account sender, Account receiver) {
            return Transfer.builder()
                    .title(transferCreate.getTitle())
                    .requestDate(LocalDateTime.now())
                    .amount(transferCreate.getAmount())
                    .currency(Currency.valueOf(transferCreate.getCurrency()))
                    .status(Status.PENDING)
                    .sender(sender)
                    .receiver(receiver).build();
        }

        public static PendingTransfer mapToPending(Transfer transfer) {
            return new PendingTransfer(transfer.getId(), transfer.getCurrency(), transfer.getAmount(), Status.PENDING);
        }

        public static HistoryTransferDTO mapToHistoryTransfer(Transfer transfer, String clientId) {
            TransferType transferType;
            if (transfer.getReceiver().getClientId().equals(clientId)) {
                transferType = TransferType.RECEIVED;
            } else {
                transferType = TransferType.SENT;
            }
            return HistoryTransferDTO.builder()
                    .transferType(transferType)
                    .sender(transfer.getSender().getAccountDetails().getFirstName()
                            + " " + transfer.getSender().getAccountDetails().getLastName())
                    .receiver(transfer.getReceiver().getAccountDetails().getFirstName()
                            + " " + transfer.getReceiver().getAccountDetails().getLastName())
                    .title(transfer.getTitle())
                    .amount(transfer.getAmount())
                    .currency(transfer.getCurrency().name())
                    .requestDate(transfer.getRequestDate())
                    .doneDate(transfer.getDoneDate())
                    .status(transfer.getStatus().name()).build();
        }
    }
}
