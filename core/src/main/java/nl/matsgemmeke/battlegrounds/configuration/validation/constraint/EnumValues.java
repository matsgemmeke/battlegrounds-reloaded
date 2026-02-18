package nl.matsgemmeke.battlegrounds.configuration.validation.constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validates that all elements inside a collection match constant names of the specified enum type.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface EnumValues {

    Class<? extends Enum<?>> type();
}
