package nl.matsgemmeke.battlegrounds.validation;

import java.util.List;

public class ValidationException extends RuntimeException {

    private final List<Violation> violations;

    public ValidationException(String message, List<Violation> violations) {
        super(message);
        this.violations = violations;
    }

    public List<Violation> getViolations() {
        return violations;
    }
}
