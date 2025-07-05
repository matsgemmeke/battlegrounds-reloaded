package nl.matsgemmeke.battlegrounds.configuration.validation;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ConditionalRequiredValidatorTest {

    @Test
    public void validateDoesNothingWhenConditionalRequiredHasExpectedValueAndFieldValueIsNotNull() throws NoSuchFieldException {
        ValidationObject object = new ValidationObject();
        ConditionalRequired annotation = object.getClass().getDeclaredField("conditionalRequiredWithExpectedValue").getAnnotation(ConditionalRequired.class);

        Map<String, Object> otherFields = Map.of("conditionalField", "expected");
        ValidationContext context = new ValidationContext("conditionalRequiredWithExpectedValue", "test", otherFields);

        ConditionalRequiredValidator validator = new ConditionalRequiredValidator();

        assertThatCode(() -> validator.validate(context, annotation)).doesNotThrowAnyException();
    }

    @Test
    public void validateDoesNothingWhenConditionalRequiredHasExpectedValueAndConditionalFieldDoesNotExist() throws NoSuchFieldException {
        ValidationObject object = new ValidationObject();
        ConditionalRequired annotation = object.getClass().getDeclaredField("conditionalRequiredWithExpectedValue").getAnnotation(ConditionalRequired.class);

        Map<String, Object> otherFields = Map.of();
        ValidationContext context = new ValidationContext("conditionalRequiredWithExpectedValue", "test", otherFields);

        ConditionalRequiredValidator validator = new ConditionalRequiredValidator();

        assertThatCode(() -> validator.validate(context, annotation)).doesNotThrowAnyException();
    }

    @Test
    public void validateThrowsValidatioExceptionWhenConditionalRequiredHasExpectedValueAndFieldValueIsNull() throws NoSuchFieldException {
        ValidationObject object = new ValidationObject();
        ConditionalRequired annotation = object.getClass().getDeclaredField("conditionalRequiredWithExpectedValue").getAnnotation(ConditionalRequired.class);

        Map<String, Object> otherFields = Map.of("conditionalField", "expected");
        ValidationContext context = new ValidationContext("conditionalRequiredWithExpectedValue", null, otherFields);

        ConditionalRequiredValidator validator = new ConditionalRequiredValidator();

        assertThatThrownBy(() -> validator.validate(context, annotation))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Field 'conditionalRequiredWithExpectedValue' is required when 'conditionalField' equals to 'expected'");
    }

    @Test
    public void validateDoesNothingWhenConditionalRequiredHasNoExpectedValueAndFieldValueIsNotNull() throws NoSuchFieldException {
        ValidationObject object = new ValidationObject();
        ConditionalRequired annotation = object.getClass().getDeclaredField("conditionalRequiredWithoutExpectedValue").getAnnotation(ConditionalRequired.class);

        Map<String, Object> otherFields = Map.of("conditionalField", "expected");
        ValidationContext context = new ValidationContext("conditionalRequiredWithoutExpectedValue", "test", otherFields);

        ConditionalRequiredValidator validator = new ConditionalRequiredValidator();

        assertThatCode(() -> validator.validate(context, annotation)).doesNotThrowAnyException();
    }

    @Test
    public void validateDoesNothingWhenConditionalRequiredHasNoExpectedValueAndConditionalFieldDoesNotExist() throws NoSuchFieldException {
        ValidationObject object = new ValidationObject();
        ConditionalRequired annotation = object.getClass().getDeclaredField("conditionalRequiredWithoutExpectedValue").getAnnotation(ConditionalRequired.class);

        Map<String, Object> otherFields = Map.of();
        ValidationContext context = new ValidationContext("conditionalRequiredWithoutExpectedValue", null, otherFields);

        ConditionalRequiredValidator validator = new ConditionalRequiredValidator();

        assertThatCode(() -> validator.validate(context, annotation)).doesNotThrowAnyException();
    }

    @Test
    public void validateThrowsValidatioExceptionWhenConditionalRequiredHasNoExpectedValueAndFieldValueIsNull() throws NoSuchFieldException {
        ValidationObject object = new ValidationObject();
        ConditionalRequired annotation = object.getClass().getDeclaredField("conditionalRequiredWithoutExpectedValue").getAnnotation(ConditionalRequired.class);

        Map<String, Object> otherFields = Map.of("conditionalField", "expected");
        ValidationContext context = new ValidationContext("conditionalRequiredWithoutExpectedValue", null, otherFields);

        ConditionalRequiredValidator validator = new ConditionalRequiredValidator();

        assertThatThrownBy(() -> validator.validate(context, annotation))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Field 'conditionalRequiredWithoutExpectedValue' is required when 'conditionalField' is set");
    }
}
