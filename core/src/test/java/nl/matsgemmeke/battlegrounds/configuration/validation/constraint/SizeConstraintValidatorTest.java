package nl.matsgemmeke.battlegrounds.configuration.validation.constraint;

import nl.matsgemmeke.battlegrounds.configuration.validation.ValidationContext;
import nl.matsgemmeke.battlegrounds.configuration.validation.ValidationException;
import nl.matsgemmeke.battlegrounds.configuration.validation.ValidationObject;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SizeConstraintValidatorTest {

    private final SizeConstraintValidator validator = new SizeConstraintValidator();

    @Test
    void validateDoesNothingWhenFieldValueIsNull() throws NoSuchFieldException {
        ValidationObject object = new ValidationObject();
        Size annotation = object.getClass().getDeclaredField("sizeWithExact").getAnnotation(Size.class);
        ValidationContext context = new ValidationContext("sizeWithExact", null, Map.of());

        assertThatCode(() -> validator.validate(context, annotation)).doesNotThrowAnyException();
    }

    @Test
    void validateThrowsValidationExceptionWhenFieldValueIsNoCollectionOrArray() throws NoSuchFieldException {
        ValidationObject object = new ValidationObject();
        Size annotation = object.getClass().getDeclaredField("sizeWithExact").getAnnotation(Size.class);
        ValidationContext context = new ValidationContext("sizeWithExact", "not a collection or array", Map.of());

        assertThatThrownBy(() -> validator.validate(context, annotation))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Value must be a collection or an array, but was String");
    }

    @Test
    void validateThrowsValidationExceptionWhenSizeDoesNotEqualExact() throws NoSuchFieldException {
        ValidationObject object = new ValidationObject();
        Size annotation = object.getClass().getDeclaredField("sizeWithExact").getAnnotation(Size.class);
        ValidationContext context = new ValidationContext("sizeWithExact", List.of(1, 2, 3), Map.of());

        assertThatThrownBy(() -> validator.validate(context, annotation))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Amount of items must be exactly 1");
    }

    @Test
    void validateThrowsValidationExceptionWhenSizeIsLessThanMin() throws NoSuchFieldException {
        ValidationObject object = new ValidationObject();
        Size annotation = object.getClass().getDeclaredField("sizeWithMinMax").getAnnotation(Size.class);
        ValidationContext context = new ValidationContext("sizeWithMinMax", new double[] {}, Map.of());

        assertThatThrownBy(() -> validator.validate(context, annotation))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Amount of items must be greater than 1");
    }

    @Test
    void validateThrowsValidationExceptionWhenSizeIsGreaterThanMax() throws NoSuchFieldException {
        ValidationObject object = new ValidationObject();
        Size annotation = object.getClass().getDeclaredField("sizeWithMinMax").getAnnotation(Size.class);
        ValidationContext context = new ValidationContext("sizeWithMinMax", new double[] {1, 2, 3}, Map.of());

        assertThatThrownBy(() -> validator.validate(context, annotation))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Amount of items must be less than 2");
    }
}
