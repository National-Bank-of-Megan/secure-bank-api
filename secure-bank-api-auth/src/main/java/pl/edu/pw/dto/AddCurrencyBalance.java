package pl.edu.pw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
public class AddCurrencyBalance {

    @NotBlank(message = "Currency name cannot be blank")
    @ValidCurrency(message = "Unknown currency provided")
    private String currency;

    @NotNull(message = "Currency amount is not specified")
    @Digits(integer = 6, fraction = 2, message = "Invalid money amount format")
    @DecimalMin(value = "0.0", inclusive = false, message = "Money amount cannot be negative")
    @DecimalMax(value = "100000.0", message = "You can deposit up to 100 000 at once")
    private BigDecimal amount;
}
