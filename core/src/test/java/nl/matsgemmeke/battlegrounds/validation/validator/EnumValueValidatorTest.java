package nl.matsgemmeke.battlegrounds.validation.validator;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class EnumValueValidatorTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @ParameterizedTest
    @ValueSource(strings = { "FLAME" })
    @NullSource
    @DisplayName("isValid returns true when given value is a valid enum type")
    void isValid_validEnumValue(String value) {
        TestValidationObject object = new TestValidationObject();
        object.enumValue = value;

        Set<ConstraintViolation<TestValidationObject>> violations = validator.validate(object);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("isValid returns false when given value is not a valid type of enum")
    void isValid_invalidValueLargeEnum() {
        TestValidationObject object = new TestValidationObject();
        object.enumValue = "fail";

        Set<ConstraintViolation<TestValidationObject>> violations = validator.validate(object);

        assertThat(violations).satisfiesExactly(violation -> {
            assertThat(violation.getMessage()).isEqualTo("invalid value 'fail' for enum Particle");
        });
    }
}
