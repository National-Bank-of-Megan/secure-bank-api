package pl.edu.pw.dto;

import lombok.Data;
import pl.edu.pw.security.validation.Money;
import pl.edu.pw.security.validation.ValidCurrency;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Data
public class TransferCreate {

    @NotNull
    @Size(min = 3, max = 60)
    private String title;

    @Pattern(regexp="[\\d]{26}")
    private String receiverAccountNumber;

    @Money
    private BigDecimal amount;

    @NotBlank
    @ValidCurrency
    private String currency;
}
