package pl.edu.pw.core.dto;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import pl.edu.pw.core.model.MoneyBalanceOperation;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@SuperBuilder
public class TransferDTO extends MoneyBalanceOperation {
    private TransferType transferType;
    private String sender;
    private String receiver;
    private String title;
    private BigDecimal amount;
    private String currency;
    private LocalDateTime doneDate;
    private String status;
}
