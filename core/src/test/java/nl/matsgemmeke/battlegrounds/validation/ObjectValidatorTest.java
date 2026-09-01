package nl.matsgemmeke.battlegrounds.validation;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ObjectValidatorTest {

    private Validator validator;
    private ObjectValidator objectValidator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        objectValidator = new ObjectValidator(validator);
    }

    @Test
    @DisplayName("validate throws ValidationException when given object has constraint violations")
    void validate_invalidObject() {
        TestValidationObject validationObject = new TestValidationObject();
        validationObject.enumValue = "fail";

        assertThatThrownBy(() -> objectValidator.validate(validationObject))
                .isInstanceOfSatisfying(ValidationException.class, exception -> {
                    assertThat(exception.getViolations()).satisfiesExactly(violation -> {
                        assertThat(violation.propertyPath()).isEqualTo("enumValue");
                        assertThat(violation.message()).isEqualTo("invalid value \"fail\" for enum Particle");
                    });
                })
                .hasMessage("Validation failed for object TestValidationObject (1 constraint violation)");
    }

    @Test
    @DisplayName("validate does nothing when given object has no constraint violations")
    void validate_validObject() {
        TestValidationObject validationObject = new TestValidationObject();

        assertThatCode(() -> objectValidator.validate(validationObject)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("validateValue throws ValidationException when given object has constraint violations")
    void validateValue_invalidObject() {
        assertThatThrownBy(() -> objectValidator.validateValue(TestValidationObject.class, "enumValue", "fail"))
                .isInstanceOfSatisfying(ValidationException.class, exception -> {
                    assertThat(exception.getViolations()).satisfiesExactly(violation -> {
                        assertThat(violation.propertyPath()).isEqualTo("enumValue");
                        assertThat(violation.message()).isEqualTo("invalid value \"fail\" for enum Particle");
                    });
                })
                .hasMessage("Validation failed for value enumValue (1 constraint violation)");
    }

    @Test
    @DisplayName("validateValue does nothing when given object has no constraint violations")
    void validateValue_validObject() {
        assertThatCode(() -> objectValidator.validateValue(TestValidationObject.class, "enumValue", "FLAME")).doesNotThrowAnyException();
    }
}
