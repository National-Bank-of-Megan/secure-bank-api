package pl.edu.pw.dto;

import lombok.Data;

@Data
public class TransferCreate {
    private String title;
    private String receiverId;
    private String receiverName;
    private String senderId;
    private double amount;
    private String currency;
}
