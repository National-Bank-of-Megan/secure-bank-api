package pl.edu.pw.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferCreate {
    private String title;
    private String senderId;
    private String receiverAccountNumber;
    private BigDecimal amount;
    private String currency;
}
