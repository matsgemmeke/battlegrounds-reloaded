package nl.matsgemmeke.battlegrounds.configuration.validation.validator;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import nl.matsgemmeke.battlegrounds.configuration.item.equipment.EquipmentSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.SpecDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ValidDroppingValidatorTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    @DisplayName("isValid returns true when drop controls are null")
    void isValid_nullDropControls() {
        EquipmentSpec equipmentSpec = createEquipmentSpec();
        equipmentSpec.controls.drop = null;

        Set<ConstraintViolation<EquipmentSpec>> violations = validator.validate(equipmentSpec);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("isValid returns false when dropping properties are null")
    void isValid_nullDroppingProperties() {
        EquipmentSpec equipmentSpec = createEquipmentSpec();
        equipmentSpec.deploy.dropping = null;

        Set<ConstraintViolation<EquipmentSpec>> violations = validator.validate(equipmentSpec);

        assertThat(violations).satisfiesExactly(violation -> {
            assertThat(violation.getPropertyPath().toString()).isEqualTo("deploy.dropping");
            assertThat(violation.getMessage()).isEqualTo("drop controls are enabled, therefore a configuration for deploy.dropping is required");
        });
    }

    @Test
    @DisplayName("isValid returns false when drop item is null")
    void isValid_nullDropItem() {
        EquipmentSpec equipmentSpec = createEquipmentSpec();
        equipmentSpec.items.dropItem = null;

        Set<ConstraintViolation<EquipmentSpec>> violations = validator.validate(equipmentSpec);

        assertThat(violations).satisfiesExactly(violation -> {
            assertThat(violation.getPropertyPath().toString()).isEqualTo("items.dropItem");
            assertThat(violation.getMessage()).isEqualTo("drop controls are enabled, therefore a configuration for items.drop-item is required");
        });
    }

    @Test
    @DisplayName("isValid returns true when given specification is valid")
    void isValid_validSpec() {
        EquipmentSpec equipmentSpec = createEquipmentSpec();

        Set<ConstraintViolation<EquipmentSpec>> violations = validator.validate(equipmentSpec);

        assertThat(violations).isEmpty();
    }

    private static EquipmentSpec createEquipmentSpec() {
        File file = new File("src/main/resources/items/lethal_equipment/frag_grenade.yml");

        SpecDeserializer specDeserializer = new SpecDeserializer();
        return specDeserializer.deserializeSpec(file, EquipmentSpec.class);
    }
}
