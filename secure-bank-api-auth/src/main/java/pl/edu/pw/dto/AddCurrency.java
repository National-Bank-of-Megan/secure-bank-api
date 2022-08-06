package pl.edu.pw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddCurrency {

    @NotBlank
    private String currency;

    @NotBlank
    private BigDecimal amount;
}
