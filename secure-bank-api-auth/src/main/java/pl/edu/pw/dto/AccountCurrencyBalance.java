package pl.edu.pw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountCurrencyBalance {
    @NotBlank
    private String currency;

    @NotBlank
    private double balance;
}
