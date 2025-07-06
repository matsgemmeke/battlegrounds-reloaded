package nl.matsgemmeke.battlegrounds.configuration.validation;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ConditionalRequiredValidatorTest {

    @Test
    public void validateDoesNothingWhenConditionalFieldHasNoValue() throws NoSuchFieldException {
        ValidationObject object = new ValidationObject();
        ConditionalRequired annotation = object.getClass().getDeclaredField("conditionalRequiredWithMatchValue").getAnnotation(ConditionalRequired.class);

        Map<String, Object> otherFields = Map.of();
        ValidationContext context = new ValidationContext("conditionalRequiredWithMatchValue", null, otherFields);

        ConditionalRequiredValidator validator = new ConditionalRequiredValidator();

        assertThatCode(() -> validator.validate(context, annotation)).doesNotThrowAnyException();
    }

    @Test
    public void validateDoesNothingWhenFieldValueIsNotNull() throws NoSuchFieldException {
        ValidationObject object = new ValidationObject();
        ConditionalRequired annotation = object.getClass().getDeclaredField("conditionalRequiredWithMatchValue").getAnnotation(ConditionalRequired.class);

        Map<String, Object> otherFields = Map.of("conditionalField", "test");
        ValidationContext context = new ValidationContext("conditionalRequiredWithMatchValue", "the value", otherFields);

        ConditionalRequiredValidator validator = new ConditionalRequiredValidator();

        assertThatCode(() -> validator.validate(context, annotation)).doesNotThrowAnyException();
    }

    @Test
    public void validateThrowsValidationExceptionWhenConditionalFieldWithoutMatchValueExistsAndFieldValueIsNull() throws NoSuchFieldException {
        ValidationObject object = new ValidationObject();
        ConditionalRequired annotation = object.getClass().getDeclaredField("conditionalRequiredWithoutMatchValue").getAnnotation(ConditionalRequired.class);

        Map<String, Object> otherFields = Map.of("conditionalField", "test");
        ValidationContext context = new ValidationContext("conditionalRequiredWithoutMatchValue", null, otherFields);

        ConditionalRequiredValidator validator = new ConditionalRequiredValidator();

        assertThatThrownBy(() -> validator.validate(context, annotation))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Field 'conditionalRequiredWithoutMatchValue' is required when 'conditionalField' is set");
    }

    @Test
    public void validateDoesNothingWhenConditionalFieldWithMatchValueExistsWithValueThatDoesNotMatchAndFieldValueIsNull() throws NoSuchFieldException {
        ValidationObject object = new ValidationObject();
        ConditionalRequired annotation = object.getClass().getDeclaredField("conditionalRequiredWithMatchValue").getAnnotation(ConditionalRequired.class);

        Map<String, Object> otherFields = Map.of("conditionalField", "value that does not match");
        ValidationContext context = new ValidationContext("conditionalRequiredWithMatchValue", null, otherFields);

        ConditionalRequiredValidator validator = new ConditionalRequiredValidator();

        assertThatCode(() -> validator.validate(context, annotation)).doesNotThrowAnyException();
    }

    @Test
    public void validateThrowsValidationExceptionWhenConditionalFieldWithMatchValueExistsAndFieldValueIsNull() throws NoSuchFieldException {
        ValidationObject object = new ValidationObject();
        ConditionalRequired annotation = object.getClass().getDeclaredField("conditionalRequiredWithMatchValue").getAnnotation(ConditionalRequired.class);

        Map<String, Object> otherFields = Map.of("conditionalField", "test");
        ValidationContext context = new ValidationContext("conditionalRequiredWithMatchValue", null, otherFields);

        ConditionalRequiredValidator validator = new ConditionalRequiredValidator();

        assertThatThrownBy(() -> validator.validate(context, annotation))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Field 'conditionalRequiredWithMatchValue' is required when 'conditionalField' equals 'test'");
    }
}
