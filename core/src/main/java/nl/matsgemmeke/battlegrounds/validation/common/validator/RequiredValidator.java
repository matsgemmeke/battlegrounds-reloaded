package nl.matsgemmeke.battlegrounds.validation.common.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import nl.matsgemmeke.battlegrounds.validation.common.constraint.Required;

public class RequiredValidator implements ConstraintValidator<Required, Object> {

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext constraintValidatorContext) {
        return object != null;
    }
}
