package nl.matsgemmeke.battlegrounds.validation.constraint;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Pattern(regexp = "^#([a-fA-F0-9]{6}|[a-fA-F0-9]{3})$")
@Constraint(validatedBy = {})
@ReportAsSingleViolation
@Target({ ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface HexColor {

    String message() default "value '${validatedValue}' is not a valid hex color";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
