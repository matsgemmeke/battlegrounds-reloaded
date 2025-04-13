package nl.matsgemmeke.battlegrounds.configuration.validation;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class RequiredValidatorTest {

    @Test
    public void validateReturnsNoErrorWhenGivenValueIsNotNull() {
        RequiredValidator<String> validator = new RequiredValidator<>();
        Optional<String> error = validator.validate("test", "any value");

        assertThat(error).isEmpty();
    }

    @Test
    public void validateReturnsErrorMessageWhenGivenValueIsNull() {
        RequiredValidator<String> validator = new RequiredValidator<>();
        Optional<String> error = validator.validate("test", null);

        assertThat(error).hasValue("Missing required 'test' value");
    }
}
