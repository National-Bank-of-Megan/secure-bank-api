package pl.edu.pw.dto;

import com.sun.istack.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import pl.edu.pw.security.validation.Money;
import pl.edu.pw.security.validation.ValidCurrency;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class CurrencyExchangeRequest {

    @NotBlank
    @ValidCurrency
    private String currencyBought;

    @NotBlank
    @ValidCurrency
    private String currencySold;

    @NotNull
    @DateTimeFormat
    private LocalDateTime exchangeTime;

    @NotNull
    @Digits(integer = 6, fraction = 2)
    @DecimalMin(value = "0.0", inclusive = false)
    @DecimalMax(value = "100000.0")
    private BigDecimal sold;
}
