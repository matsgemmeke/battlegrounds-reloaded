package nl.matsgemmeke.battlegrounds.configuration.validation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class RequiredIfFieldExistsValidator<T> implements Validator<T> {

    @Nullable
    private final Object conditionValue;
    @NotNull
    private final String conditionRoute;

    public RequiredIfFieldExistsValidator(@NotNull String conditionRoute, @Nullable Object conditionValue) {
        this.conditionRoute = conditionRoute;
        this.conditionValue = conditionValue;
    }

    @NotNull
    public Optional<String> validate(@NotNull String route, @Nullable T value) {
        if (value == null && conditionValue != null) {
            return Optional.of("Value at '%s' is required when '%s' is set".formatted(route, conditionRoute));
        }

        return Optional.empty();
    }
}
