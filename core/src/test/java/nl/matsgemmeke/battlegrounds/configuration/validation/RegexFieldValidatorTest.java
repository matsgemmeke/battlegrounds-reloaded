package nl.matsgemmeke.battlegrounds.configuration.validation;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class RegexFieldValidatorTest {

    @Test
    public void validateDoesNothingWhenFieldValueIsNull() throws NoSuchFieldException {
        ValidationObject object = new ValidationObject();
        Regex annotation = object.getClass().getDeclaredField("regex").getAnnotation(Regex.class);
        ValidationContext context = new ValidationContext("regex", null, Map.of());

        RegexFieldValidator validator = new RegexFieldValidator();

        assertThatCode(() -> validator.validate(context, annotation)).doesNotThrowAnyException();
    }

    @Test
    public void validateDoesNothingWhenFieldValueHasCorrectPattern() throws NoSuchFieldException {
        ValidationObject object = new ValidationObject();
        Regex annotation = object.getClass().getDeclaredField("regex").getAnnotation(Regex.class);
        ValidationContext context = new ValidationContext("regex", "abc", Map.of());

        RegexFieldValidator validator = new RegexFieldValidator();

        assertThatCode(() -> validator.validate(context, annotation)).doesNotThrowAnyException();
    }

    @Test
    public void validateThrowsValidationExceptionWhenFieldValueDoesNotHaveCorrectPattern() throws NoSuchFieldException {
        ValidationObject object = new ValidationObject();
        Regex annotation = object.getClass().getDeclaredField("regex").getAnnotation(Regex.class);
        ValidationContext context = new ValidationContext("regex", "def", Map.of());

        RegexFieldValidator validator = new RegexFieldValidator();

        assertThatThrownBy(() -> validator.validate(context, annotation))
                .isInstanceOf(ValidationException.class)
                .hasMessage("The value 'def' for field 'regex' does not match the required pattern");
    }
}
