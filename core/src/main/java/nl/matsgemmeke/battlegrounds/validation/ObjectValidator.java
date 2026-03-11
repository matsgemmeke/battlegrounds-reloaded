package nl.matsgemmeke.battlegrounds.validation;

import com.google.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.util.Set;
import java.util.stream.Collectors;

public class ObjectValidator {

    private final Validator validator;

    @Inject
    public ObjectValidator(Validator validator) {
        this.validator = validator;
    }

    public <T> void validate(T object) {
        Set<ConstraintViolation<T>> violations = validator.validate(object);

        if (violations.isEmpty()) {
            return;
        }

        String violationsMessage = violations.stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining("\n - ", " - ", ""));
        String errorMessage = "Validation failed for %s (%d constraint violations):\n%s"
                .formatted(object.getClass().getSimpleName(), violations.size(), violationsMessage);

        throw new ValidationException(errorMessage);
    }
}
