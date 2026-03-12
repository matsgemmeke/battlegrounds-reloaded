package nl.matsgemmeke.battlegrounds.validation.common.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import nl.matsgemmeke.battlegrounds.validation.common.constraint.EnumValue;

import java.util.Arrays;

public class EnumValueValidator implements ConstraintValidator<EnumValue, Object> {

    private static final String MESSAGE_TEMPLATE = "Invalid value '%s' for enum %s. Allowed values: %s";
    private static final int LARGE_ALLOWED_VALUES_SIZE = 10;

    private Class<? extends Enum<?>> type;

    @Override
    public void initialize(EnumValue constraintAnnotation) {
        this.type = constraintAnnotation.type();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        }

        if (Arrays.stream(type.getEnumConstants()).anyMatch(e -> e.name().equals(value.toString()))) {
            return true;
        }

        String allowedValues = this.formatAllowedValues(type);
        String message = MESSAGE_TEMPLATE.formatted(value, type.getSimpleName(), allowedValues);

        constraintValidatorContext.disableDefaultConstraintViolation();
        constraintValidatorContext.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        return false;
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
