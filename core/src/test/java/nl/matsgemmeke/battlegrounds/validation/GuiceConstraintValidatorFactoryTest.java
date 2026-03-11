package nl.matsgemmeke.battlegrounds.validation;

import com.google.inject.Injector;
import jakarta.validation.ConstraintValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GuiceConstraintValidatorFactoryTest {

    @Mock
    private Injector injector;
    @InjectMocks
    private GuiceConstraintValidatorFactory constraintValidatorFactory;

    @Test
    @DisplayName("getInstance returns instance with injected services from guice")
    void getInstance_returnsGuiceInstance() {
        TestValidator testValidator = new TestValidator();

        when(injector.getInstance(TestValidator.class)).thenReturn(testValidator);

        ConstraintValidator<?, ?> constraintValidator = constraintValidatorFactory.getInstance(TestValidator.class);

        assertThat(constraintValidator).isSameAs(testValidator);
    }
}
