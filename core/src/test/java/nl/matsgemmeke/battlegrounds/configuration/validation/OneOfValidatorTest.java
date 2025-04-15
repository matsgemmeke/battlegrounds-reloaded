package nl.matsgemmeke.battlegrounds.configuration.validation;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class OneOfValidatorTest {

    private static final List<String> ALLOWED_VALUES = List.of("TEST_ONE", "TEST_TWO");

    @Test
    public void validateReturnsNoErrorWhenGivenValueIsPresentInAllowedValues() {
        OneOfValidator<String> validator = new OneOfValidator<>(ALLOWED_VALUES);
        Optional<String> error = validator.validate("test", "TEST_ONE");

        assertThat(error).isEmpty();
    }

    @Test
    public void validateReturnsErrorMessageWhenGivenValueIsNotPresetInAllowedValues() {
        OneOfValidator<String> validator = new OneOfValidator<>(ALLOWED_VALUES);
        Optional<String> error = validator.validate("test", "fail");

        assertThat(error).hasValue("Invalid value 'fail' at route 'test'. Expected one of [TEST_ONE, TEST_TWO]");
    }
}
