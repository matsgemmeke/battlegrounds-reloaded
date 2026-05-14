package nl.matsgemmeke.battlegrounds.configuration.validation.constraint;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import nl.matsgemmeke.battlegrounds.configuration.validation.validator.ValidDroppingValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ValidDroppingValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDropping {

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
