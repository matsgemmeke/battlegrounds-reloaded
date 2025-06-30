package nl.matsgemmeke.battlegrounds.configuration.validation;

import nl.matsgemmeke.battlegrounds.configuration.item.gun.ItemSpec;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ObjectValidatorTest {

    @Test
    public void validateThrowsIllegalAccessExceptionWhenValidatingObjectWithPrivateFields() {
        PrivateSpec privateSpec = new PrivateSpec();

        assertThatThrownBy(() -> ObjectValidator.validate(privateSpec))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Cannot validate field 'secret' because it is not marked public");
    }

    @Test
    public void validateDoesNothingWhenGivenObjectIsValid() {
        ItemSpec itemSpec = new ItemSpec();
        itemSpec.material = "IRON_HOE";
        itemSpec.displayName = "Test Item";
        itemSpec.damage = 1;

        ObjectValidator.validate(itemSpec);
    }
}
