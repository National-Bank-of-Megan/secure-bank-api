package pl.edu.pw.dto;

import com.sun.istack.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class CurrencyExchangeRequest {

    @NotNull
    private String currencyBought;

    @NotNull
    private String currencySold;

    @NotNull
    private LocalDateTime exchangeTime;

    @NotNull
    private BigDecimal sold;

}
