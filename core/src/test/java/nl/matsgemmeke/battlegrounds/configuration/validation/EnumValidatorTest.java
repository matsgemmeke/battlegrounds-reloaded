package nl.matsgemmeke.battlegrounds.configuration.validation;

import org.bukkit.Material;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class EnumValidatorTest {

    @Test
    public void validateReturnsNoErrorMessageWhenGivenValueIsNull() {
        EnumValidator<Material> validator = new EnumValidator<>(Material.class);
        Optional<String> error = validator.validate("test", null);

        assertThat(error).isEmpty();
    }

    @Test
    public void validateReturnsNoErrorMessageWhenGivenValueIsValid() {
        EnumValidator<Material> validator = new EnumValidator<>(Material.class);
        Optional<String> error = validator.validate("test", "STONE");

        assertThat(error).isEmpty();
    }

    @Test
    public void validateReturnsErrorMessageWhenGivenValueIsInvalid() {
        EnumValidator<Material> validator = new EnumValidator<>(Material.class);
        Optional<String> error = validator.validate("test", "invalid");

        assertThat(error).hasValue("Invalid Material value 'invalid' at route 'test'");
    }
}
