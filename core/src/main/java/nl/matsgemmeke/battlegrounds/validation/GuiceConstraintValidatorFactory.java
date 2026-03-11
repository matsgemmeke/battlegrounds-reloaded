package nl.matsgemmeke.battlegrounds.validation;

import com.google.inject.Injector;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorFactory;

public class GuiceConstraintValidatorFactory implements ConstraintValidatorFactory {

    private final Injector injector;

    public GuiceConstraintValidatorFactory(Injector injector) {
        this.injector = injector;
    }

    @Override
    public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> type) {
        return injector.getInstance(type);
    }

    @Override
    public void releaseInstance(ConstraintValidator<?, ?> constraintValidator) {
        // No op
    }
}
