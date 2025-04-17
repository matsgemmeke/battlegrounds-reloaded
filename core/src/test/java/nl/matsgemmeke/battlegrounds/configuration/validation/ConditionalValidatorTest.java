package nl.matsgemmeke.battlegrounds.configuration.validation;

import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ConditionalValidatorTest {

    @Test
    public void validateReturnsNoErrorMessageWhenValueIsNotNull() {
        ConditionalRequiredValidator<Integer, String> validator = new ConditionalRequiredValidator<>("fire-mode", "FULLY_AUTOMATIC", Set.of());
        Optional<String> error = validator.validate("rate-of-fire", 600);

        assertThat(error).isEmpty();
    }

    @Test
    public void validateReturnsNoErrorMessageWhenConditionValueDoesNotEqualAnyExpectedValues() {
        ConditionalRequiredValidator<Integer, String> validator = new ConditionalRequiredValidator<>("fire-mode", "FULLY_AUTOMATIC", Set.of("SEMI_AUTOMATIC"));
        Optional<String> error = validator.validate("rate-of-fire", null);

        assertThat(error).isEmpty();
    }

    @Test
    public void validateReturnsErrorMessageWhenValueIsNullAndConditionValueEqualsExpectedValue() {
        ConditionalRequiredValidator<Integer, String> validator = new ConditionalRequiredValidator<>("fire-mode", "FULLY_AUTOMATIC", Set.of("FULLY_AUTOMATIC"));
        Optional<String> error = validator.validate("rate-of-fire", null);

        assertThat(error).hasValue("Missing required value at 'rate-of-fire' when 'fire-mode' equals 'FULLY_AUTOMATIC'");
    }
}
