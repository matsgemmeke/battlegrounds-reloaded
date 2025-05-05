package nl.matsgemmeke.battlegrounds.configuration.validation;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class GreaterThanOrEqualToFieldValidatorTest {

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = { 5 })
    public void validateReturnsErrorMessageWhenValueIsLowerThanFieldValue(Integer value) {
        String fieldRoute = "field";
        Integer fieldValue = 10;

        GreaterThanOrEqualToFieldValidator<Integer> validator = new GreaterThanOrEqualToFieldValidator<>(fieldRoute, fieldValue);
        Optional<String> error = validator.validate("target", value);

        assertThat(error).hasValue("The value '%s' at route 'target' must be greater than or equal to the value '10' at route 'field'".formatted(value));
    }

    @ParameterizedTest
    @ValueSource(ints = { 10, 11 })
    public void validateReturnsNoErrorWhenValueIsHigherOrEqualToFieldValue(Integer value) {
        String fieldRoute = "field";
        Integer fieldValue = 10;

        GreaterThanOrEqualToFieldValidator<Integer> validator = new GreaterThanOrEqualToFieldValidator<>(fieldRoute, fieldValue);
        Optional<String> error = validator.validate("target", value);

        assertThat(error).isEmpty();
    }
}
