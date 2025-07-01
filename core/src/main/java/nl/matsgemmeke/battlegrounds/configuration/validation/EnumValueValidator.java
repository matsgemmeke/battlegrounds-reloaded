package nl.matsgemmeke.battlegrounds.configuration.validation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class EnumValueValidator implements FieldValidator<EnumValue> {

    public void validate(@NotNull String name, @Nullable Object value, @NotNull EnumValue annotation) {
        if (value == null) {
            return;
        }

        Class<? extends Enum<?>> type = annotation.value();

        if (Arrays.stream(type.getEnumConstants()).anyMatch(e -> e.name().equals(value))) {
            return;
        }

        throw new ValidationException("Invalid %s value '%s' for field '%s'".formatted(type.getSimpleName(), value, name));
    }
}
