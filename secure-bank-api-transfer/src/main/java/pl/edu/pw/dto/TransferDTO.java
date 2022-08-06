package pl.edu.pw.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Builder
public class TransferDTO {
    private String receiver;
    private String title;
    private BigDecimal amount;
    private String currency;
    private Date requestDate;
    private Date doneDate;
    private String status;
    private BigDecimal balanceAfter;
}
