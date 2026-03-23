package nl.matsgemmeke.battlegrounds.validation;

import com.google.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.util.Set;
import java.util.stream.Collectors;

public class ObjectValidator {

    private static final String PROPERTY_VIOLATION_FORMAT = "%s: %s";

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
                .map(this::createViolationMessage)
                .collect(Collectors.joining("\n - ", " - ", ""));
        String errorMessage = "Validation failed for %s (%d constraint violations):\n%s"
                .formatted(object.getClass().getSimpleName(), violations.size(), violationsMessage);

        throw new ValidationException(errorMessage);
    }

    private <T> String createViolationMessage(ConstraintViolation<T> violation) {
        String propertyPath = convertCamelCaseToKebabCase(violation.getPropertyPath().toString());

        return PROPERTY_VIOLATION_FORMAT.formatted(propertyPath, violation.getMessage());
    }

    private static String convertCamelCaseToKebabCase(String input) {
        return input
                .replaceAll("([A-Z])(?=[A-Z])", "$1-")
                .replaceAll("([a-z])([A-Z])", "$1-$2")
                .toLowerCase();
    }
}
