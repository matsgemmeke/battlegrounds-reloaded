package nl.matsgemmeke.battlegrounds.validation.common.validator;

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
        object.enumValueLargeEnum = value;

        Set<ConstraintViolation<TestValidationObject>> violations = validator.validate(object);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("isValid returns false when given value is not a valid type of a small enum")
    void isValid_invalidValueSmallEnum() {
        TestValidationObject object = new TestValidationObject();
        object.enumValueSmallEnum = "fail";

        Set<ConstraintViolation<TestValidationObject>> violations = validator.validate(object);

        assertThat(violations).satisfiesExactly(violation -> {
            assertThat(violation.getMessage()).isEqualTo("Invalid value 'fail' for enum HitboxComponentType. Allowed values: [HEAD, TORSO, LIMBS]");
        });
    }

    @Test
    @DisplayName("isValid returns false when given value is not a valid type of a large enum")
    void isValid_invalidValueLargeEnum() {
        TestValidationObject object = new TestValidationObject();
        object.enumValueLargeEnum = "fail";

        Set<ConstraintViolation<TestValidationObject>> violations = validator.validate(object);

        assertThat(violations).satisfiesExactly(violation -> {
            assertThat(violation.getMessage()).isEqualTo("Invalid value 'fail' for enum Particle. Allowed values: [EXPLOSION_NORMAL, EXPLOSION_LARGE, EXPLOSION_HUGE, FIREWORKS_SPARK, WATER_BUBBLE, WATER_SPLASH, WATER_WAKE, SUSPENDED, SUSPENDED_DEPTH, CRIT] ... (92 more)");
        });
    }
}
