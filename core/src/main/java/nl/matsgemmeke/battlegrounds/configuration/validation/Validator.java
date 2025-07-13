package nl.matsgemmeke.battlegrounds.configuration.validation;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;

/**
 * Represents a validation rule that can be applied to a configuration value.
 * <p>
 * A {@code Validator} checks whether a given value meets certain criteria and, if not, throws a validation error
 * describing the failure.
 *
 * @param <T> the annotation type that is used on the value being validated
 */
public interface Validator<T extends Annotation> {

    /**
     * Validates a field on an object.
     * <p>
     * If the field is valid, this method will not throw any errors. If the field is invalid, a validation error such
     * as {@link ValidationException} will be thrown, depending on the implementing class.
     *
     * @param context the context of the validation
     * @param annotation the annotation placed on the field that is being validated
     */
    void validate(@NotNull ValidationContext context, @NotNull T annotation);
}
