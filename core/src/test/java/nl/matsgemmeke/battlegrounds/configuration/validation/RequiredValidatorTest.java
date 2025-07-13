package nl.matsgemmeke.battlegrounds.configuration.validation;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class RequiredValidatorTest {

    @Test
    public void validateThrowsValidationExceptionWhenValueIsNull() throws NoSuchFieldException {
        ValidationObject object = new ValidationObject();
        Required annotation = object.getClass().getDeclaredField("required").getAnnotation(Required.class);
        ValidationContext context = new ValidationContext("required", null, Map.of());

        RequiredValidator validator = new RequiredValidator();

        assertThatThrownBy(() -> validator.validate(context, annotation))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Field 'required' is required but no value is provided");
    }

    @Test
    public void validateDoesNothingWhenValueIsNotNull() throws NoSuchFieldException {
        ValidationObject object = new ValidationObject();
        Required annotation = object.getClass().getDeclaredField("required").getAnnotation(Required.class);
        ValidationContext context = new ValidationContext("required", "test", Map.of());

        RequiredValidator validator = new RequiredValidator();

        assertThatCode(() -> validator.validate(context, annotation)).doesNotThrowAnyException();
    }
}
