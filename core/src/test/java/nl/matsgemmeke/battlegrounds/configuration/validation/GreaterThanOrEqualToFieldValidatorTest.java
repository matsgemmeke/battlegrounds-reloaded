package nl.matsgemmeke.battlegrounds.configuration.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class GreaterThanOrEqualToFieldValidatorTest {

    @ParameterizedTest
    @CsvSource(value = {"null,10", "10,null"}, nullValues = "null")
    public void validateReturnsNoErrorWhenEitherValueIsNull(Integer value, Integer fieldValue) {
        String fieldRoute = "field";

        GreaterThanOrEqualToFieldValidator<Integer> validator = new GreaterThanOrEqualToFieldValidator<>(fieldRoute, fieldValue);
        Optional<String> error = validator.validate("target", value);

        assertThat(error).isEmpty();
    }

    @Test
    public void validateReturnsErrorMessageWhenValueIsLowerThanFieldValue() {
        String fieldRoute = "field";
        Integer fieldValue = 10;

        GreaterThanOrEqualToFieldValidator<Integer> validator = new GreaterThanOrEqualToFieldValidator<>(fieldRoute, fieldValue);
        Optional<String> error = validator.validate("target", 5);

        assertThat(error).hasValue("The value '5' at route 'target' must be greater than or equal to the value '10' at route 'field'");
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
