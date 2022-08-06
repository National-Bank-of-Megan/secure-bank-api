package pl.edu.pw.dto;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
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
    private BigDecimal amountBought;

    @NotNull
    private String currencySold;

    @NotNull
    private BigDecimal amountSold;


}
