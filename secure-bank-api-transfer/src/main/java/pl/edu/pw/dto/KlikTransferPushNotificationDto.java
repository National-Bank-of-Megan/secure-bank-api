package pl.edu.pw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class KlikTransferPushNotificationDto {
    private String title;
    private String moneyReceiverAccountNumber;
    private BigDecimal amount;
    private String currency;
    private LocalDateTime dateCreated;
}
