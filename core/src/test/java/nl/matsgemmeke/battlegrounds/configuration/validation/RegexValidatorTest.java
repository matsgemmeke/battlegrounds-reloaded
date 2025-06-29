package nl.matsgemmeke.battlegrounds.configuration.validation;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class RegexValidatorTest {

    private static final String SIMPLE_REGEX = "[abc]+";

    @Test
    public void validateReturnsNoErrorMessageWhenGivenValueIsNull() {
        RegexValidator validator = new RegexValidator(SIMPLE_REGEX);
        Optional<String> error = validator.validate("test", null);

        assertThat(error).isEmpty();
    }

    @Test
    public void validateReturnsNoErrorMessageWhenGivenValueMatchesRegex() {
        RegexValidator validator = new RegexValidator(SIMPLE_REGEX);
        Optional<String> error = validator.validate("test", "abc");

        assertThat(error).isEmpty();
    }

    @Test
    public void validateReturnsErrorMessageWhenGivenValueDoesNotMatchRegex() {
        RegexValidator validator = new RegexValidator(SIMPLE_REGEX);
        Optional<String> error = validator.validate("test", "def");

        assertThat(error).hasValue("The value 'def' at route 'test' does not match the required format");
    }
}
