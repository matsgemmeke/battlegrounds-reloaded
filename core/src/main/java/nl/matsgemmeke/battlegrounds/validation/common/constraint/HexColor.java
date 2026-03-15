package nl.matsgemmeke.battlegrounds.validation.common.constraint;

import jakarta.validation.Constraint;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Pattern(regexp = "^#([a-fA-F0-9]{6}|[a-fA-F0-9]{3})$")
@Constraint(validatedBy = {})
@Target({ ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface HexColor {
}
