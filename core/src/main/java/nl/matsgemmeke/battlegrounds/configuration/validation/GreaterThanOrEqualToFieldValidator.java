package nl.matsgemmeke.battlegrounds.configuration.validation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class GreaterThanOrEqualToFieldValidator<T extends Comparable<T>> implements Validator<T> {

    @NotNull
    private final String fieldRoute;
    @NotNull
    private final T fieldValue;

    public GreaterThanOrEqualToFieldValidator(@NotNull String fieldRoute, @NotNull T fieldValue) {
        this.fieldRoute = fieldRoute;
        this.fieldValue = fieldValue;
    }

    @NotNull
    public Optional<String> validate(@NotNull String route, @Nullable T value) {
        if (value == null || value.compareTo(fieldValue) < 0) {
            return Optional.of("The value '%s' at route '%s' must be greater than or equal to the value '%s' at route '%s'".formatted(value, route, fieldValue, fieldRoute));
        }

        return Optional.empty();
    }
}
