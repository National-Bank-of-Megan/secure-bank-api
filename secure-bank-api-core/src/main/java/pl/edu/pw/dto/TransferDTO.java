package pl.edu.pw.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import pl.edu.pw.model.MoneyBalanceOperation;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@SuperBuilder
public class TransferDTO extends MoneyBalanceOperation {
    private String sender;
    private String receiver;
    private String title;
    private BigDecimal amount;
    private String currency;
    private LocalDateTime doneDate;
    private String status;
    private BigDecimal balanceAfter;
}
