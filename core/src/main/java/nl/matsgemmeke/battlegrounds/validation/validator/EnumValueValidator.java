package nl.matsgemmeke.battlegrounds.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import nl.matsgemmeke.battlegrounds.validation.constraint.EnumValue;

import java.util.Arrays;

public class EnumValueValidator implements ConstraintValidator<EnumValue, Object> {

    private static final String MESSAGE_TEMPLATE = "invalid value \"%s\" for enum %s";

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

        String message = MESSAGE_TEMPLATE.formatted(value, type.getSimpleName());

        constraintValidatorContext.disableDefaultConstraintViolation();
        constraintValidatorContext.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        return false;
    }
}
