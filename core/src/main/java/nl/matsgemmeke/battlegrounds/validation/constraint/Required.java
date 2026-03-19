package nl.matsgemmeke.battlegrounds.validation.constraint;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import nl.matsgemmeke.battlegrounds.validation.validator.RequiredValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = RequiredValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Required {

    String message() default "value is required";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
