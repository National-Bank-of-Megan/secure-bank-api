package pl.edu.pw.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
public class TransferDTO {
    private String receiver;
    private String title;
    private double amount;
    private String currency;
    private Date requestDate;
    private Date doneDate;
    private String status;
    private double balanceAfter;
}
