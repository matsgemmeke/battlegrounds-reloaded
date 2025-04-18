package nl.matsgemmeke.battlegrounds.configuration.validation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;

/**
 * Validates that a value is present (non-null) only if another specific value is one of a given set of possibilities.
 * <p>
 * This validator is useful for enforcing conditional presence rules, such as requiring a field to be set only when
 * another field has a specific value.
 *
 * @param <T> the type of the value being validated
 * @param <S> the type of the condition value
 */
public class ConditionalRequiredValidator<T, S> implements Validator<T> {

    @NotNull
    private final S conditionValue;
    @NotNull
    private final Set<S> expectedValues;
    @NotNull
    private final String conditionRoute;

    public ConditionalRequiredValidator(@NotNull String conditionRoute, @NotNull S conditionValue, @NotNull S expectedValue) {
        this(conditionRoute, conditionValue, Set.of(expectedValue));
    }

    public ConditionalRequiredValidator(@NotNull String conditionRoute, @NotNull S conditionValue, @NotNull Set<S> expectedValues) {
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
