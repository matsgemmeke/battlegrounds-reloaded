package nl.matsgemmeke.battlegrounds.configuration.validation;

import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class RequiredIfFieldEqualsValidatorTest {

    @Test
    public void validateReturnsNoErrorMessageWhenValueIsNotNull() {
        RequiredIfFieldEqualsValidator<Integer, String> validator = new RequiredIfFieldEqualsValidator<>("fire-mode", "FULLY_AUTOMATIC", Set.of());
        Optional<String> error = validator.validate("rate-of-fire", 600);

        assertThat(error).isEmpty();
    }

    @Test
    public void validateReturnsNoErrorMessageWhenConditionValueDoesNotEqualAnyExpectedValues() {
        RequiredIfFieldEqualsValidator<Integer, String> validator = new RequiredIfFieldEqualsValidator<>("fire-mode", "FULLY_AUTOMATIC", Set.of("SEMI_AUTOMATIC"));
        Optional<String> error = validator.validate("rate-of-fire", null);

        assertThat(error).isEmpty();
    }

    @Test
    public void validateReturnsErrorMessageWhenValueIsNullAndConditionValueEqualsExpectedValue() {
        RequiredIfFieldEqualsValidator<Integer, String> validator = new RequiredIfFieldEqualsValidator<>("fire-mode", "FULLY_AUTOMATIC", Set.of("FULLY_AUTOMATIC"));
        Optional<String> error = validator.validate("rate-of-fire", null);

        assertThat(error).hasValue("Value at 'rate-of-fire' is required when 'fire-mode' is set to 'FULLY_AUTOMATIC'");
    }
}
