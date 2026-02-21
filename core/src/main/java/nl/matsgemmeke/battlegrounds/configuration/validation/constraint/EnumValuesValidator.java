package nl.matsgemmeke.battlegrounds.configuration.validation.constraint;

import nl.matsgemmeke.battlegrounds.configuration.validation.ValidationContext;
import nl.matsgemmeke.battlegrounds.configuration.validation.ValidationException;
import nl.matsgemmeke.battlegrounds.configuration.validation.Validator;

import java.util.Arrays;
import java.util.Collection;

public class EnumValuesValidator implements Validator<EnumValues> {

    private static final int LARGE_ALLOWED_VALUES_SIZE = 10;

    @Override
    public void validate(ValidationContext context, EnumValues annotation) {
        Object fieldValue = context.fieldValue();

        if (fieldValue == null) {
            return;
        }

        if (!(fieldValue instanceof Collection<?> collection)) {
            throw new ValidationException("Value must be a collection, but was a " + fieldValue.getClass().getSimpleName());
        }

        Class<? extends Enum<?>> type = annotation.type();

        for (Object element : collection) {
            if (Arrays.stream(type.getEnumConstants()).noneMatch(e -> e.name().equals(element))) {
                String enumType = type.getSimpleName();
                String allowedValues = this.formatAllowedValues(type);

                throw new ValidationException("Invalid value '%s' for enum %s. Allowed values: %s".formatted(element, enumType, allowedValues));
            }
        }
    }

    private String formatAllowedValues(Class<? extends Enum<?>> type) {
        Enum<?>[] constants = type.getEnumConstants();
        int size = constants.length;

        if (size >= LARGE_ALLOWED_VALUES_SIZE) {
            return Arrays.stream(constants).limit(LARGE_ALLOWED_VALUES_SIZE).toList() + " ... (" + (size - LARGE_ALLOWED_VALUES_SIZE) + " more)";
        } else {
            return Arrays.stream(constants).map(Enum::name).toList().toString();
        }
    }
}
