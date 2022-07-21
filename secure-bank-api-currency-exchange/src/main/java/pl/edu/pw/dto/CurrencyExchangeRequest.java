package pl.edu.pw.dto;

import com.sun.istack.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@Data
public class CurrencyExchangeRequest {

    @NotNull
    private String currencyBought;

    @NotNull
    private String currencySold;

    @NotNull
//    x sold = 1 bought
//    if exchangeRate != CurrencyAPI.getRateOnTime(exchangeTime) -> block account for fraud
    private double exchangeRate;

    @NotNull
    private Date exchangeTime;

    @NotNull
    private double bought;

    @NotNull
    private double sold;

}
