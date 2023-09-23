package pl.edu.pw.transfer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TransferNotificationDto {
    private String title;
    private String senderFirstName;
    private String senderLastName;
    private BigDecimal amount;
    private String currency;
    private LocalDateTime arrivalDate;
}
