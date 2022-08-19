package pl.edu.pw.dto;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pl.edu.pw.model.MoneyBalanceOperation;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class CurrencyExchangeDto extends MoneyBalanceOperation {

    public CurrencyExchangeDto(LocalDateTime requestDate, String currencyBought, BigDecimal amountBought, String currencySold, BigDecimal amountSold) {
        super(requestDate);
        this.currencyBought = currencyBought;
        this.amountBought = amountBought;
        this.currencySold = currencySold;
        this.amountSold = amountSold;
    }

    @NotNull
    private String currencyBought;

    @NotNull
    private BigDecimal amountBought;

    @NotNull
    private String currencySold;

    @NotNull
    private BigDecimal amountSold;
}
