package nl.matsgemmeke.battlegrounds.configuration.validation;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class EnumValueValidatorTest {

    @Test
    public void validateDoesNothingWhenGivenValueIsNull() throws NoSuchFieldException {
        ValidationObject object = new ValidationObject();
        EnumValue annotation = object.getClass().getDeclaredField("enumValue").getAnnotation(EnumValue.class);
        ValidationContext context = new ValidationContext("enumValue", null, Map.of());

        EnumValueValidator validator = new EnumValueValidator();

        assertThatCode(() -> validator.validate(context, annotation)).doesNotThrowAnyException();
    }

    @Test
    public void validateDoesNothingWhenGivenValueEqualsOneOfTheEnumValues() throws NoSuchFieldException {
        ValidationObject object = new ValidationObject();
        EnumValue annotation = object.getClass().getDeclaredField("enumValue").getAnnotation(EnumValue.class);
        ValidationContext context = new ValidationContext("enumValue", "FLAME", Map.of());

        EnumValueValidator validator = new EnumValueValidator();

        assertThatCode(() -> validator.validate(context, annotation)).doesNotThrowAnyException();
    }

    @Test
    public void validateThrowsValidationExceptionWhenGivenValueDoesNotEqualOneOfTheEnumValues() throws NoSuchFieldException {
        ValidationObject object = new ValidationObject();
        EnumValue annotation = object.getClass().getDeclaredField("enumValue").getAnnotation(EnumValue.class);
        ValidationContext context = new ValidationContext("enumValue", "fail", Map.of());

        EnumValueValidator validator = new EnumValueValidator();

        assertThatThrownBy(() -> validator.validate(context, annotation))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Invalid Particle value 'fail' for field 'enumValue'");
    }
}
