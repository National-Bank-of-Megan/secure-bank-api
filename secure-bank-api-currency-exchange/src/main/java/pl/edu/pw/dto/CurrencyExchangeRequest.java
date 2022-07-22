package pl.edu.pw.dto;

import com.sun.istack.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
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
    private double sold;

}
