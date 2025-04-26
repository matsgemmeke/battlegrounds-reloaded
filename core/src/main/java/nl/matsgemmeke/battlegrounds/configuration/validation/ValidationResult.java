package nl.matsgemmeke.battlegrounds.configuration.validation;

public class ValidationResult<T> {

    private final T value;
    private final String errorMessage;

    private ValidationResult(T value, String errorMessage) {
        this.value = value;
        this.errorMessage = errorMessage;
    }

    public static <T> ValidationResult<T> success(T value) {
        return new ValidationResult<>(value, null);
    }

    public static <T> ValidationResult<T> error(String error) {
        return new ValidationResult<>(null, error);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public T getValue() {
        return value;
    }

    public boolean isValid() {
        return errorMessage == null;
    }
}
