package nl.matsgemmeke.battlegrounds.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import nl.matsgemmeke.battlegrounds.validation.constraint.Required;

public class RequiredValidator implements ConstraintValidator<Required, Object> {

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext constraintValidatorContext) {
        return object != null;
    }
}
