package pl.edu.pw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.edu.pw.security.validation.Money;
import pl.edu.pw.security.validation.ValidCurrency;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddCurrency {

    @NotBlank
    @ValidCurrency
    private String currency;

    @Money
    private BigDecimal amount;
}
