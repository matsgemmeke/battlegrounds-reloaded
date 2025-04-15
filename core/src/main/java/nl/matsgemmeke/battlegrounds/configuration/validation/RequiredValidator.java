package nl.matsgemmeke.battlegrounds.configuration.validation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class RequiredValidator<T> implements Validator<T> {

    @NotNull
    public Optional<String> validate(@NotNull String route, @Nullable T value) {
        if (value == null) {
            return Optional.of("Missing required '%s' value".formatted(route));
        }

        return Optional.empty();
    }
}
