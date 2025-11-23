package nl.matsgemmeke.battlegrounds.configuration.validation.constraint;

import nl.matsgemmeke.battlegrounds.configuration.validation.ValidationContext;
import nl.matsgemmeke.battlegrounds.configuration.validation.ValidationException;
import nl.matsgemmeke.battlegrounds.configuration.validation.Validator;

import java.lang.reflect.Array;
import java.util.Collection;

public class SizeConstraintValidator implements Validator<Size> {

    @Override
    public void validate(ValidationContext context, Size annotation) {
        Object fieldValue = context.fieldValue();
        int min = annotation.min();
        int max = annotation.max();
        int exact = annotation.exact();

        if (fieldValue == null) {
            return;
        }

        int size;

        if (fieldValue instanceof Collection<?>) {
            size = ((Collection<?>) fieldValue).size();
        } else if (fieldValue.getClass().isArray()) {
            size = Array.getLength(fieldValue);
        } else {
            throw new ValidationException("Value must be a collection or an array, but was " + fieldValue.getClass().getSimpleName());
        }

        if (exact >= 0 && size != exact) {
            throw new ValidationException("Amount of items must be exactly " + exact);
        }

        if (size < min) {
            throw new ValidationException("Amount of items must be greater than " + min);
        }

        if (size > max) {
            throw new ValidationException("Amount of items must be less than " + max);
        }
    }
}
