package nl.matsgemmeke.battlegrounds.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TestValidator implements ConstraintValidator<TestConstraint, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return true;
    }
}
