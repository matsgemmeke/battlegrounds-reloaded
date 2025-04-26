package nl.matsgemmeke.battlegrounds.configuration.validation;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class MapOneOfValidatorTest {

    private static final List<String> ALLOWED_VALUES = List.of("TEST_ONE", "TEST_TWO");

    @Test
    public void validateReturnsNoErrorMessageWhenGivenValueIsNull() {
        MapOneOfValidator<String, Integer> validator = new MapOneOfValidator<>(ALLOWED_VALUES);
        Optional<String> error = validator.validate("test", null);

        assertThat(error).isEmpty();
    }

    @Test
    public void validateReturnsNoErrorWhenGivenValueIsPresentInAllowedValues() {
        Map<String, Integer> values = Map.of("TEST_ONE", 1);

        MapOneOfValidator<String, Integer> validator = new MapOneOfValidator<>(ALLOWED_VALUES);
        Optional<String> error = validator.validate("test", values);

        assertThat(error).isEmpty();
    }

    @Test
    public void validateReturnsErrorWhenGivenValueIsNotPresentInAllowedValues() {
        Map<String, Integer> values = Map.of("fail", 1);

        MapOneOfValidator<String, Integer> validator = new MapOneOfValidator<>(ALLOWED_VALUES);
        Optional<String> error = validator.validate("test", values);

        assertThat(error).hasValue("Invalid key 'fail' at route 'test.fail'. Expected one of [TEST_ONE, TEST_TWO]");
    }
}
