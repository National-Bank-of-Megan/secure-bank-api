package dto;

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
    private String exchangeRate;

    @NotNull
    private Date exchangeTime;

    @NotNull
    private double bought;

    @NotNull
    private double sold;

}
