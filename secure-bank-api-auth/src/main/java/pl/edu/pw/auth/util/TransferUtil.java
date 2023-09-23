package pl.edu.pw.auth.util;

import lombok.experimental.UtilityClass;
import pl.edu.pw.core.domain.Currency;

@UtilityClass
public class TransferUtil {
    public Currency getCurrencyFromString(String stringCurrency) {
        Currency currency;
        try {
            currency = Currency.valueOf(stringCurrency.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown currency " + stringCurrency);
        }
        return currency;
    }
}
