package pl.edu.pw.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import pl.edu.pw.security.validation.ValidCurrency;

import javax.validation.constraints.*;
import java.math.BigDecimal;

@Builder(access = AccessLevel.PUBLIC)
@Getter
public class PaymentRequest {

    @Pattern(regexp = "[\\d]{26}", message = "Klik code must contain digits only and be 6 digits long")
    private String code;

    @NotBlank(message = "Payment title cannot be blank")
    @Size(min = 3, max = 60, message = "Payment title must be from 3 to 60 characters long")
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
