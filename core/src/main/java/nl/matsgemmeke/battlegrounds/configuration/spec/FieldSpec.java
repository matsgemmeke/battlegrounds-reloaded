package nl.matsgemmeke.battlegrounds.configuration.spec;

import nl.matsgemmeke.battlegrounds.configuration.validation.ValidationResult;
import nl.matsgemmeke.battlegrounds.configuration.validation.Validator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents the specification for a single configuration field.
 * <p>
 * Once configured, the {@link #getValidatedValue(T)} method can be used to validate a given value, returning a
 * {@link ValidationResult} that either contains the validated value or an error message if validation fails.
 *
 * @param <T> the type of the configuration value
 */
public class FieldSpec<T> {

    @NotNull
    private final String route;
    @NotNull
    private final List<Validator<T>> validators;

    public FieldSpec(@NotNull String route) {
        this.route = route;
        this.validators = new ArrayList<>();
    }

    /**
     * Adds a validator to this field specification.
     * <p>
     * Validators are applied in the order they are added. Each validator checks a condition on the given value. If any
     * validator fails, the validation process will stop and return an error result.
     *
     * @param validator the validator
     * @return this {@code FieldSpec} instance, allowing for method chaining
     */
    @NotNull
    public FieldSpec<T> withValidator(@NotNull Validator<T> validator) {
        validators.add(validator);
        return this;
    }

    /**
     * Validates a value for this field. If a validator fails, an error message is returned via a
     * {@link ValidationResult}; otherwise, the validated value is returned.
     *
     * @return a {@link ValidationResult} containing either the valid value or an error message
     */
    @NotNull
    public ValidationResult<T> getValidatedValue(@Nullable T value) {
        for (Validator<T> validator : validators) {
            Optional<String> errorMessage = validator.validate(route, value);

            if (errorMessage.isPresent()) {
                return ValidationResult.error(errorMessage.get());
            }
        }
        return ValidationResult.success(value);
    }
}
