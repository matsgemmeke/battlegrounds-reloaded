package nl.matsgemmeke.battlegrounds.i18n.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = {})
@NotBlank(message = "chat lines must contain a text value")
@Size(max = 256)
@Target({ ElementType.FIELD, ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ChatLine {

    String message() default "invalid chat line";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
