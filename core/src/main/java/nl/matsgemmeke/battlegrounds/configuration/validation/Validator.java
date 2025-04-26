package nl.matsgemmeke.battlegrounds.configuration.validation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Represents a validation rule that can be applied to a configuration value.
 * <p>
 * A {@code Validator} checks whether a given value meets certain criteria and, if not, returns an error message
 * describing the failure.
 *
 * @param <T> the type of the value to validate
 */
public interface Validator<T> {

    /**
     * Validates the given value and returns an optional error message.
     * <p>
     * If the value is valid, this method returns {@link Optional#empty()}. If the value is invalid, it returns an
     * {@link Optional} containing a descriptive error message. The {@code route} parameter provides the path to the
     * value in the configuration, allowing the validator to include it in error messages for better context.
     *
     * @param route the path to the value in the configuration
     * @param value the value to validate (may be {@code null})
     * @return an {@link Optional} containing an error message if validation fails, or empty if valid
     */
    @NotNull
    Optional<String> validate(@NotNull String route, @Nullable T value);
}
