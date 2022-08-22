package pl.edu.pw.dto;

import com.sun.istack.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import pl.edu.pw.security.validation.Money;
import pl.edu.pw.security.validation.ValidCurrency;

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

    @Money
    private BigDecimal sold;
}
