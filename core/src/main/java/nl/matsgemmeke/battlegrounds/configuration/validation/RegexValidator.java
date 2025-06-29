package nl.matsgemmeke.battlegrounds.configuration.validation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class RegexValidator implements Validator<String> {

    @NotNull
    private final String regex;

    public RegexValidator(@NotNull String regex) {
        this.regex = regex;
    }

    @NotNull
    public Optional<String> validate(@NotNull String route, @Nullable String value) {
        if (value != null && !value.matches(regex)) {
            return Optional.of("The value '%s' at route '%s' does not match the required format".formatted(value, route));
        }

        return Optional.empty();
    }
}
