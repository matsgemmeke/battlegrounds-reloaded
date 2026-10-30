package nl.matsgemmeke.battlegrounds.validation;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public final class TestValidatorFactory {

    public static ObjectValidator createObjectValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        return new ObjectValidator(validator);
    }

    public static ObjectValidator createObjectValidator(Module module) {
        Injector injector = Guice.createInjector(module);
        GuiceConstraintValidatorFactory constraintValidatorFactory = new GuiceConstraintValidatorFactory(injector);

        Validator validator = Validation.byDefaultProvider()
                .configure()
                .constraintValidatorFactory(constraintValidatorFactory)
                .buildValidatorFactory()
                .getValidator();

        return new ObjectValidator(validator);
    }
}
