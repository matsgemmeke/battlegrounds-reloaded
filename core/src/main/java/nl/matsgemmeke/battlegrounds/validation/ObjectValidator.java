package nl.matsgemmeke.battlegrounds.validation;

import com.google.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import nl.matsgemmeke.battlegrounds.util.TextUtil;

import java.util.List;
import java.util.Set;

public class ObjectValidator {

    private final Validator validator;

    @Inject
    public ObjectValidator(Validator validator) {
        this.validator = validator;
    }

    public <T> void validate(T object) {
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(object);

        if (constraintViolations.isEmpty()) {
            return;
        }

        String violationNoun = TextUtil.pluralize(constraintViolations.size(), "violation", "violations");
        String errorMessage = "Validation failed for object %s (%d constraint %s)".formatted(object.getClass().getSimpleName(), constraintViolations.size(), violationNoun);
        List<Violation> violations = constraintViolations.stream().map(this::createViolationObject).toList();

        throw new ValidationException(errorMessage, violations);
    }

    public <T> void validateValue(Class<T> objectType, String valueName, Object value) {
        Set<ConstraintViolation<T>> constraintViolations = validator.validateValue(objectType, valueName, value);

        if (constraintViolations.isEmpty()) {
            return;
        }

        String violationNoun = TextUtil.pluralize(constraintViolations.size(), "violation", "violations");
        String errorMessage = "Validation failed for value %s (%d constraint %s)".formatted(valueName, constraintViolations.size(), violationNoun);
        List<Violation> violations = constraintViolations.stream().map(this::createViolationObject).toList();

        throw new ValidationException(errorMessage, violations);
    }

    private Violation createViolationObject(ConstraintViolation<?> constraintViolation) {
        String propertyPath = constraintViolation.getPropertyPath().toString();
        String message = constraintViolation.getMessage();

        return new Violation(propertyPath, message);
    }
}
