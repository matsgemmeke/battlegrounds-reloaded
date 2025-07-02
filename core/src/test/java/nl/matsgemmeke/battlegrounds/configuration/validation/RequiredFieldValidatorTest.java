package nl.matsgemmeke.battlegrounds.configuration.validation;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class RequiredFieldValidatorTest {

    @Test
    public void validateThrowsValidationExceptionWhenValueIsNull() throws NoSuchFieldException {
        ValidationObject object = new ValidationObject();
        Required annotation = object.getClass().getDeclaredField("required").getAnnotation(Required.class);
        ValidationContext context = new ValidationContext("required", null, Map.of());

        RequiredFieldValidator validator = new RequiredFieldValidator();

        assertThatThrownBy(() -> validator.validate(context, annotation))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Field 'required' is required but no value is provided");
    }

    @Test
    public void validateDoesNothingWhenValueIsNotNull() throws NoSuchFieldException {
        ValidationObject object = new ValidationObject();
        Required annotation = object.getClass().getDeclaredField("required").getAnnotation(Required.class);
        ValidationContext context = new ValidationContext("required", "test", Map.of());

        RequiredFieldValidator validator = new RequiredFieldValidator();

        assertThatCode(() -> validator.validate(context, annotation)).doesNotThrowAnyException();
    }
}
