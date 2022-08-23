package pl.edu.pw.dto;

import lombok.Data;
import pl.edu.pw.security.validation.Money;
import pl.edu.pw.security.validation.ValidCurrency;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
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

    @NotNull
    @Digits(integer = 6, fraction = 2)
    @DecimalMin(value = "0.0", inclusive = false)
    @DecimalMax(value = "100000.0")
    private BigDecimal amount;

    @NotBlank
    @ValidCurrency
    private String currency;
}
