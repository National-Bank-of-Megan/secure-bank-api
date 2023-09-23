package pl.edu.pw.transfer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.edu.pw.auth.security.validation.ValidCurrency;

import javax.validation.constraints.*;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferCreate {

    @NotBlank(message = "Transfer title cannot be blank")
    @Size(min = 3, max = 60, message = "Transfer title must be from 3 to 60 characters long")
    private String title;

    @Pattern(regexp = "[\\d]{26}", message = "Account number must contain digits only and be 26 digits long")
    private String receiverAccountNumber;

    @NotNull(message = "Amount to transfer is not specified")
    @Digits(integer = 6, fraction = 2, message = "Invalid money amount format")
    @DecimalMin(value = "0.0", inclusive = false, message = "Money amount cannot be negative")
    @DecimalMax(value = "100000.0", message = "You can transfer up to 100 000 at once")
    private BigDecimal amount;

    @NotBlank(message = "Currency name cannot be blank")
    @ValidCurrency(message = "Unknown currency provided")
    private String currency;
}
