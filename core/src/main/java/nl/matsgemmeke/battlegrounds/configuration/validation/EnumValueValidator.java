package nl.matsgemmeke.battlegrounds.configuration.validation;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class EnumValueValidator implements Validator<EnumValue> {

    public void validate(@NotNull ValidationContext context, @NotNull EnumValue annotation) {
        Object fieldValue = context.fieldValue();

        if (fieldValue == null) {
            return;
        }

        Class<? extends Enum<?>> type = annotation.type();

        if (Arrays.stream(type.getEnumConstants()).anyMatch(e -> e.name().equals(fieldValue))) {
            return;
        }

        throw new ValidationException("Invalid %s value '%s' for field '%s'".formatted(type.getSimpleName(), fieldValue, context.fieldName()));
    }
}
