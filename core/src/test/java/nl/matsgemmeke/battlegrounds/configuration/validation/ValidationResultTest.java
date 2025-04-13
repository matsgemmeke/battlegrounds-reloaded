package nl.matsgemmeke.battlegrounds.configuration.validation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ValidationResultTest {

    @Test
    public void getErrorMessageReturnsNullWhenResultIsSuccessful() {
        ValidationResult<String> validationResult = ValidationResult.success("success");

        assertThat(validationResult.getErrorMessage()).isNull();
    }

    @Test
    public void getErrorMessageReturnsErrorMessageWhenResultHasError() {
        String errorMessage = "error";

        ValidationResult<String> validationResult = ValidationResult.error(errorMessage);

        assertThat(validationResult.getErrorMessage()).isEqualTo(errorMessage);
    }

    @Test
    public void getValueReturnsValueWhenResultIsSuccessful() {
        String value = "success";

        ValidationResult<String> validationResult = ValidationResult.success(value);

        assertThat(validationResult.getValue()).isEqualTo(value);
    }

    @Test
    public void getValueReturnsNullWhenResultHasError() {
        ValidationResult<String> validationResult = ValidationResult.error("error");

        assertThat(validationResult.getValue()).isNull();
    }

    @Test
    public void isValidReturnsTrueWhenResultIsSuccessful() {
        ValidationResult<String> validationResult = ValidationResult.success("success");

        assertThat(validationResult.isValid()).isTrue();
    }

    @Test
    public void isValidReturnsFalseWhenResultHasError() {
        ValidationResult<String> validationResult = ValidationResult.error("error");

        assertThat(validationResult.isValid()).isFalse();
    }
}
