package nl.matsgemmeke.battlegrounds.configuration.spec;

import nl.matsgemmeke.battlegrounds.configuration.validation.ValidationResult;
import nl.matsgemmeke.battlegrounds.configuration.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FieldSpecTest {

    @Test
    public void getValidatedValueReturnsSuccessResultWhenAllValidatorsPass() {
        String route = "route.to.value";
        String value = "test";

        Validator<String> validator = mock();
        when(validator.validate(route, value)).thenReturn(Optional.empty());

        FieldSpec<String> fieldSpec = new FieldSpec<String>("name").withValidator(validator);
        ValidationResult<String> result = fieldSpec.getValidatedValue(value);

        assertThat(result.getValue()).isEqualTo(value);
    }

    @Test
    public void getValidatedValueReturnsErrorResultWhenOneValidatorFails() {
        String error = "error";
        String route = "route.to.value";
        String value = "test";

        Validator<String> validator = mock();
        when(validator.validate(route, value)).thenReturn(Optional.of(error));

        FieldSpec<String> fieldSpec = new FieldSpec<String>(route).withValidator(validator);
        ValidationResult<String> result = fieldSpec.getValidatedValue(value);

        assertThat(result.getErrorMessage()).isEqualTo(error);
    }
}
