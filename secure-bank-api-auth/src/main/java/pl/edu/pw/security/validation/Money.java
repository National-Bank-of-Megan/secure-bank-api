package pl.edu.pw.security.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@NotNull
@Digits(integer = 6, fraction = 2)
@DecimalMin(value = "0.0", inclusive = false)
@DecimalMax(value = "100000.0")
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
public @interface Money {
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
