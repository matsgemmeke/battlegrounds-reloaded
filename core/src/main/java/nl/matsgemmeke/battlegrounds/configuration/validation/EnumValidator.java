package nl.matsgemmeke.battlegrounds.configuration.validation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class EnumValidator<T extends Enum<T>> implements Validator<String> {

    @NotNull
    private final Class<T> enumType;

    public EnumValidator(@NotNull Class<T> enumType) {
        this.enumType = enumType;
    }

    @NotNull
    public Optional<String> validate(@NotNull String route, @Nullable String value) {
        // Pass the validation if the value is null, the RequiredValidator handles null cases
        if (value == null) {
            return Optional.empty();
        }

        try {
            Enum.valueOf(enumType, value);
            return Optional.empty();
        } catch (IllegalArgumentException e) {
            return Optional.of("Invalid %s value '%s' at route '%s'".formatted(enumType.getSimpleName(), value, route));
        }
    }
}
