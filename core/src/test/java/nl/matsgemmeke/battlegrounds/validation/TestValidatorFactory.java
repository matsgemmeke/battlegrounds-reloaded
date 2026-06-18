package nl.matsgemmeke.battlegrounds.validation;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public final class TestValidatorFactory {

    public static ObjectValidator createObjectValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        return new ObjectValidator(validator);
    }
}
