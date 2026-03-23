package nl.matsgemmeke.battlegrounds.validation;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
                .isInstanceOf(ValidationException.class)
                .hasMessage("""
                     Validation failed for TestValidationObject (1 constraint violations):
                      - enum-value: invalid value "fail" for enum Particle""");
    }

    @Test
    @DisplayName("validate does nothing when given object has no constraint violations")
    void validate_validObject() {
        TestValidationObject validationObject = new TestValidationObject();

        assertThatCode(() -> objectValidator.validate(validationObject)).doesNotThrowAnyException();
    }
}
