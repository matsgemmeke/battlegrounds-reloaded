package nl.matsgemmeke.battlegrounds.validation.common.constraint;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import nl.matsgemmeke.battlegrounds.validation.common.validator.EnumValueValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = EnumValueValidator.class)
@Target({ ElementType.FIELD, ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumValue {

    Class<? extends Enum<?>> type();

    String message() default "value is not a valid enum type";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
