package pl.edu.pw.dto;

import lombok.Data;

@Data
public class TransferCreate {
    private String title;
    private String senderId;
    private String receiverAccountNumber;
    private double amount;
    private String currency;
}
