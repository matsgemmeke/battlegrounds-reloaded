package nl.matsgemmeke.battlegrounds.configuration.validation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class RequiredFieldValidatorTest {

    @Test
    public void validateThrowsValidationExceptionWhenValueIsNull() {
        RequiredFieldValidator validator = new RequiredFieldValidator();

        assertThatThrownBy(() -> validator.validate("test", null))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Field 'test' is required but no value is provided");
    }

    @Test
    public void validateDoesNothingWhenValueIsNotNull() {
        RequiredFieldValidator validator = new RequiredFieldValidator();

        assertThatCode(() -> validator.validate("test", "value")).doesNotThrowAnyException();
    }
}
