package nl.matsgemmeke.battlegrounds.configuration.validation;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class RequiredIfFieldExistsValidatorTest {

    @Test
    public void validateReturnsErrorWhenValueIsNullAndConditionValueIsNotNull() {
        RequiredIfFieldExistsValidator<Integer> validator = new RequiredIfFieldExistsValidator<>("route", "value");
        Optional<String> error = validator.validate("test", null);

        assertThat(error).hasValue("Value at 'test' is required when 'route' is set");
    }

    @Test
    public void validateReturnsNoErrorWhenValueIsNotNull() {
        RequiredIfFieldExistsValidator<Integer> validator = new RequiredIfFieldExistsValidator<>("route", "value");
        Optional<String> error = validator.validate("test", 10);

        assertThat(error).isEmpty();
    }

    @Test
    public void validateReturnsNoErrorWhenValueIsNullAndConditionValueIsNull() {
        RequiredIfFieldExistsValidator<Integer> validator = new RequiredIfFieldExistsValidator<>("route", null);
        Optional<String> error = validator.validate("test", null);

        assertThat(error).isEmpty();
    }
}
