package nl.matsgemmeke.battlegrounds.configuration.validation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

public class RequiredFieldValidatorTest {

    @Test
    public void validateThrowsValidationExceptionWhenValueIsNull() {
        Required annotation = mock(Required.class);

        RequiredFieldValidator validator = new RequiredFieldValidator();

        assertThatThrownBy(() -> validator.validate("test", null, annotation))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Field 'test' is required but no value is provided");
    }

    @Test
    public void validateDoesNothingWhenValueIsNotNull() {
        Required annotation = mock(Required.class);

        RequiredFieldValidator validator = new RequiredFieldValidator();

        assertThatCode(() -> validator.validate("test", "value", annotation)).doesNotThrowAnyException();
    }
}
