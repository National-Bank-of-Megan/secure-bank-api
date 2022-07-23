package pl.edu.pw.dto;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CurrencyExchangeDto {

    @NotNull
    private LocalDateTime orderedOn;

    @NotNull
    private String currencyBought;

    @NotNull
    private double amountBought;

    @NotNull
    private String currencySold;

    @NotNull
    private double amountSold;


}
