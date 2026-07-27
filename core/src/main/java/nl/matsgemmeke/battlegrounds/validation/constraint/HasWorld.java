package nl.matsgemmeke.battlegrounds.validation.constraint;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import nl.matsgemmeke.battlegrounds.validation.validator.HasWorldValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = HasWorldValidator.class)
@Target({ ElementType.FIELD, ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
public @interface HasWorld {

    String message() default "location must have a world";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
