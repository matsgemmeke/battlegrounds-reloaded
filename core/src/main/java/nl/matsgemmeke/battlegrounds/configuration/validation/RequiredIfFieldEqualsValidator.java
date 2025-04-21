package nl.matsgemmeke.battlegrounds.configuration.validation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;

/**
 * Validator that enforces a conditional requirement rule: target field must be non-null or non-empty if another field
 * has a specific value.
 * <p>
 * This validator is useful for enforcing conditional presence rules, such as requiring a field to be set only when
 * another field has a specific value.
 *
 * @param <T> the type of the value being validated
 * @param <S> the type of the condition value
 */
public class RequiredIfFieldEqualsValidator<T, S> implements Validator<T> {

    @NotNull
    private final S conditionValue;
    @NotNull
    private final Set<S> expectedValues;
    @NotNull
    private final String conditionRoute;

    public RequiredIfFieldEqualsValidator(@NotNull String conditionRoute, @NotNull S conditionValue, @NotNull S expectedValue) {
        this(conditionRoute, conditionValue, Set.of(expectedValue));
    }

    public RequiredIfFieldEqualsValidator(@NotNull String conditionRoute, @NotNull S conditionValue, @NotNull Set<S> expectedValues) {
        this.conditionRoute = conditionRoute;
        this.conditionValue = conditionValue;
        this.expectedValues = expectedValues;
    }

    @NotNull
    public Optional<String> validate(@NotNull String route, @Nullable T value) {
        if (value == null && expectedValues.contains(conditionValue)) {
            return Optional.of("Value at '%s' is required when '%s' is set to '%s'".formatted(route, conditionRoute, conditionValue));
        }

        return Optional.empty();
    }
}
