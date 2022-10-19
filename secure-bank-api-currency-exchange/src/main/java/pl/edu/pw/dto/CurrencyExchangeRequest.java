package pl.edu.pw.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import pl.edu.pw.security.validation.ValidCurrency;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class CurrencyExchangeRequest {

    @NotBlank(message = "Currency bought name cannot be blank")
    @ValidCurrency(message = "Unknown bought currency provided")
    private String currencyBought;

    @NotBlank(message = "Currency sold name cannot be blank")
    @ValidCurrency(message = "Unknown sold currency provided")
    private String currencySold;

    @NotNull(message = "Exchange time field is required") // TODO: can be easily forged
    @DateTimeFormat
    private LocalDateTime exchangeTime;

    @NotNull(message = "Currency sold amount is not specified")
    @Digits(integer = 6, fraction = 2, message = "Invalid money amount format")
    @DecimalMin(value = "0.0", inclusive = false, message = "Money amount cannot be negative")
    @DecimalMax(value = "100000.0", message = "You can exchange up to 100 000 at once")
    private BigDecimal sold;
}
