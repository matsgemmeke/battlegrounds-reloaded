package nl.matsgemmeke.battlegrounds.configuration.spec;

import nl.matsgemmeke.battlegrounds.configuration.item.InvalidItemConfigurationException;
import nl.matsgemmeke.battlegrounds.configuration.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FieldSpecResolverTest {

    @Test
    public void resolveThrowsInvalidItemConfigurationExceptionWhenRouteIsNull() {
        String value = "hello";

        assertThatThrownBy(() -> new FieldSpecResolver<String>().value(value).resolve())
                .isInstanceOf(InvalidItemConfigurationException.class)
                .hasMessage("Cannot resolve value 'hello' without assigned configuration route");
    }

    @Test
    public void resolveThrowsInvalidItemConfigurationExceptionWhenValidationFails() {
        String error = "error";
        String route = "test";
        String value = "hello";

        Validator<String> validator = mock();
        when(validator.validate(route, value)).thenReturn(Optional.of(error));

        assertThatThrownBy(() -> new FieldSpecResolver<String>().route(route).value(value).validate(validator).resolve())
                .isInstanceOf(InvalidItemConfigurationException.class)
                .hasMessage(error);
    }

    @Test
    public void resolveReturnsValidValueWhenValidationPasses() {
        String route = "test";
        String value = "hello";

        Validator<String> validator = mock();
        when(validator.validate(route, value)).thenReturn(Optional.empty());

        String result = new FieldSpecResolver<String>().route(route).value(value).validate(validator).resolve();

        assertThat(result).isEqualTo(value);
    }
}
