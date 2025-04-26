package nl.matsgemmeke.battlegrounds.configuration.validation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MapOneOfValidator<T, S> implements Validator<Map<T, S>> {

    @NotNull
    private final List<T> allowedValues;

    public MapOneOfValidator(@NotNull List<T> allowedValues) {
        this.allowedValues = allowedValues;
    }

    @NotNull
    public Optional<String> validate(@NotNull String route, @Nullable Map<T, S> value) {
        // Pass the validation if the value is null, the RequiredValidator handles null cases
        if (value == null) {
            return Optional.empty();
        }

        for (T key : value.keySet()) {
            if (!allowedValues.contains(key)) {
                String valueRoute = route + "." + key;
                return Optional.of("Invalid key '%s' at route '%s'. Expected one of %s".formatted(key, valueRoute, allowedValues));
            }
        }

        return Optional.empty();
    }
}
