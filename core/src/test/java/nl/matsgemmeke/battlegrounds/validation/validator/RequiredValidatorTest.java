package nl.matsgemmeke.battlegrounds.validation.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class RequiredValidatorTest {

    private final RequiredValidator validator = new RequiredValidator();

    @ParameterizedTest
    @CsvSource(value = { "test,true", "null,false" }, nullValues = "null")
    @DisplayName("isValid returns whether given object is null")
    void isValid_returnsWhetherObjectIsNull(String value, boolean expectedValid) {
        boolean valid = validator.isValid(value, null);

        assertThat(valid).isEqualTo(expectedValid);
    }
}
