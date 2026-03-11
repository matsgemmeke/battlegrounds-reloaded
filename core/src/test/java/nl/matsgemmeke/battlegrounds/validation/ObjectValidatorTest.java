package nl.matsgemmeke.battlegrounds.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObjectValidatorTest {

    private static final String FIELD_PATH = "variableName";
    private static final Object VALIDATION_OBJECT = "hello";

    @Mock
    private Validator validator;
    @InjectMocks
    private ObjectValidator objectValidator;

    @Test
    @DisplayName("validate throws ValidationException with detailed message when given object has constraint violations")
    void validate_invalidObject() {
        Path fieldPath = mock(Path.class);
        when(fieldPath.toString()).thenReturn(FIELD_PATH);

        ConstraintViolation<Object> violation = mock();
        when(violation.getPropertyPath()).thenReturn(fieldPath);
        when(violation.getMessage()).thenReturn("validation error");

        when(validator.validate(VALIDATION_OBJECT)).thenReturn(Set.of(violation));

        assertThatThrownBy(() -> objectValidator.validate(VALIDATION_OBJECT))
                .isInstanceOf(ValidationException.class)
                .hasMessage("""
                     Validation failed for String (1 constraint violations):
                      - variableName: validation error""");
    }

    @Test
    @DisplayName("validate does nothing when given object has no constraint violations")
    void validate_validObject() {
        when(validator.validate(VALIDATION_OBJECT)).thenReturn(Set.of());

        assertThatCode(() -> objectValidator.validate(VALIDATION_OBJECT)).doesNotThrowAnyException();
    }
}
