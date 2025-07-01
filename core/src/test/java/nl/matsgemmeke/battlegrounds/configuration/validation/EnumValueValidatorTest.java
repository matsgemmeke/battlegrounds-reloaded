package nl.matsgemmeke.battlegrounds.configuration.validation;

import org.bukkit.Particle;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class EnumValueValidatorTest {

    @EnumValue(Particle.class)
    private String sample;

    @Test
    public void validateDoesNothingWhenGivenValueIsNull() throws NoSuchFieldException {
        Field field = this.getClass().getDeclaredField("sample");
        EnumValue annotation = field.getAnnotation(EnumValue.class);

        EnumValueValidator validator = new EnumValueValidator();

        assertThatCode(() -> validator.validate("test", null, annotation)).doesNotThrowAnyException();
    }

    @Test
    public void validateDoesNothingWhenGivenValueEqualsOneOfTheEnumValues() throws NoSuchFieldException {
        Field field = this.getClass().getDeclaredField("sample");
        EnumValue annotation = field.getAnnotation(EnumValue.class);

        EnumValueValidator validator = new EnumValueValidator();

        assertThatCode(() -> validator.validate("test", "FLAME", annotation)).doesNotThrowAnyException();
    }

    @Test
    public void validateThrowsValidationExceptionWhenGivenValueDoesNotEqualOneOfTheEnumValues() throws NoSuchFieldException {
        Field field = this.getClass().getDeclaredField("sample");
        EnumValue annotation = field.getAnnotation(EnumValue.class);

        EnumValueValidator validator = new EnumValueValidator();

        assertThatThrownBy(() -> validator.validate("test", "fail", annotation))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Invalid Particle value 'fail' for field 'test'");
    }
}
