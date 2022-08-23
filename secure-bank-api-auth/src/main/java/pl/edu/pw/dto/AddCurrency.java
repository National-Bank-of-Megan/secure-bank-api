package pl.edu.pw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.edu.pw.security.validation.Money;
import pl.edu.pw.security.validation.ValidCurrency;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddCurrency {

    @NotBlank
    @ValidCurrency
    private String currency;

    @NotNull
    @Digits(integer = 6, fraction = 2)
    @DecimalMin(value = "0.0", inclusive = false)
    @DecimalMax(value = "100000.0")
    private BigDecimal amount;
}
