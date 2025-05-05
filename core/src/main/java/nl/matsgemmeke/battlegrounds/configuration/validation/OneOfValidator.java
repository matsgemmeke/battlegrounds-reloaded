package nl.matsgemmeke.battlegrounds.configuration.validation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;

public class OneOfValidator<T> implements Validator<T> {

    private final Collection<T> allowedValues;

    public OneOfValidator(Collection<T> allowedValues) {
        this.allowedValues = allowedValues;
    }

    @NotNull
    public Optional<String> validate(@NotNull String route, @Nullable T value) {
        if (value != null && !allowedValues.contains(value)) {
            return Optional.of("Invalid value '%s' at route '%s'. Expected one of %s".formatted(value, route, allowedValues));
        }

        return Optional.empty();
    }
}
