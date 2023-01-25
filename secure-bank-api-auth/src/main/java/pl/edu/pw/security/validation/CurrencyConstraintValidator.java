package pl.edu.pw.security.validation;

import pl.edu.pw.domain.Currency;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CurrencyConstraintValidator implements ConstraintValidator<ValidCurrency, String> {
    @Override
    public void initialize(ValidCurrency constraintAnnotation) {

    }

    @Override
    public boolean isValid(String currency, ConstraintValidatorContext context) {
        if (currency == null || currency.isBlank()) return true;
        for (Currency currencyValue : Currency.values()) {
            if (currencyValue.name().equals(currency.toUpperCase())) {
                return true;
            }
        }
        return false;
    }
}
