package pl.edu.pw.core.dto;

import lombok.experimental.SuperBuilder;
import pl.edu.pw.core.model.MoneyBalanceOperation;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@SuperBuilder
public class HistoryTransferDTO extends MoneyBalanceOperation {
    private TransferType transferType;
    private String sender;
    private String receiver;
    private String title;
    private BigDecimal amount;
    private String currency;
    private LocalDateTime doneDate;
    private String status;
}
