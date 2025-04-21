package nl.matsgemmeke.battlegrounds.configuration.item;

import nl.matsgemmeke.battlegrounds.configuration.YamlReader;
import nl.matsgemmeke.battlegrounds.configuration.spec.equipment.EquipmentSpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EquipmentConfigurationTest {

    private YamlReader yamlReader;

    @BeforeEach
    public void setUp() {
        yamlReader = mock(YamlReader.class);
    }

    @Test
    public void createSpecThrowsInvalidItemConfigurationExceptionWhenValueFromYamlDoesNotPassValidator() {
        when(yamlReader.getString("name")).thenReturn(null);

        EquipmentConfiguration configuration = new EquipmentConfiguration(yamlReader);

        assertThatThrownBy(configuration::createSpec)
                .isInstanceOf(InvalidItemConfigurationException.class)
                .hasMessage("Missing required value at 'name'");
    }

    @Test
    public void createSpecReturnsGunSpecContainingValuesFromYamlReader() {
        String name = "Test Equipment";

        String displayItemMaterial = "SHEARS";
        String displayItemDisplayName = "Test Equipment";
        Integer displayItemDamage = 1;

        String activatorItemMaterial = "STICK";
        String activatorItemDisplayName = "Test Activator";
        Integer activatorItemDamage = 2;

        String throwItemMaterial = "STONE";
        String throwItemDisplayName = "Test Throw Item";
        Integer throwItemDamage = 3;

        Double health = 50.0;
        Double throwVelocity = 2.0;
        Long throwCooldown = 20L;
        String placeMaterial = "STICK";
        Long placeCooldown = 40L;

        String throwAction = "LEFT_CLICK";
        String cookAction = "RIGHT_CLICK";
        String placeAction = "RIGHT_CLICK";
        String activateAction = "RIGHT_CLICK";

        when(yamlReader.getString("name")).thenReturn(name);
        when(yamlReader.getString("description")).thenReturn(null);

        when(yamlReader.getString("item.display.material")).thenReturn(displayItemMaterial);
        when(yamlReader.getString("item.display.display-name")).thenReturn(displayItemDisplayName);
        when(yamlReader.getOptionalInt("item.display.damage")).thenReturn(Optional.of(displayItemDamage));

        when(yamlReader.contains("item.activator")).thenReturn(true);
        when(yamlReader.getString("item.activator.material")).thenReturn(activatorItemMaterial);
        when(yamlReader.getString("item.activator.display-name")).thenReturn(activatorItemDisplayName);
        when(yamlReader.getOptionalInt("item.activator.damage")).thenReturn(Optional.of(activatorItemDamage));

        when(yamlReader.contains("item.throw")).thenReturn(true);
        when(yamlReader.getString("item.throw.material")).thenReturn(throwItemMaterial);
        when(yamlReader.getString("item.throw.display-name")).thenReturn(throwItemDisplayName);
        when(yamlReader.getOptionalInt("item.throw.damage")).thenReturn(Optional.of(throwItemDamage));

        when(yamlReader.getString("controls.throw")).thenReturn(throwAction);
        when(yamlReader.getString("controls.cook")).thenReturn(cookAction);
        when(yamlReader.getString("controls.place")).thenReturn(placeAction);
        when(yamlReader.getString("controls.activate")).thenReturn(activateAction);

        when(yamlReader.getOptionalDouble("deploy.health")).thenReturn(Optional.of(health));
        when(yamlReader.getString("deploy.throwing.throw-sounds")).thenReturn(null);
        when(yamlReader.getOptionalDouble("deploy.throwing.velocity")).thenReturn(Optional.of(throwVelocity));
        when(yamlReader.getOptionalLong("deploy.throwing.cooldown")).thenReturn(Optional.of(throwCooldown));
        when(yamlReader.getString("deploy.throwing.cook-sounds")).thenReturn(null);
        when(yamlReader.getString("deploy.placing.material")).thenReturn(placeMaterial);
        when(yamlReader.getString("deploy.placing.place-sounds")).thenReturn(null);
        when(yamlReader.getOptionalLong("deploy.placing.cooldown")).thenReturn(Optional.of(placeCooldown));

        EquipmentConfiguration configuration = new EquipmentConfiguration(yamlReader);
        EquipmentSpec spec = configuration.createSpec();

        assertThat(spec.name()).isEqualTo(name);
        assertThat(spec.description()).isNull();

        assertThat(spec.displayItemSpec().material()).isEqualTo(displayItemMaterial);
        assertThat(spec.displayItemSpec().displayName()).isEqualTo(displayItemDisplayName);
        assertThat(spec.displayItemSpec().damage()).isEqualTo(displayItemDamage);

        assertThat(spec.activatorItemSpec().material()).isEqualTo(activatorItemMaterial);
        assertThat(spec.activatorItemSpec().displayName()).isEqualTo(activatorItemDisplayName);
        assertThat(spec.activatorItemSpec().damage()).isEqualTo(activatorItemDamage);

        assertThat(spec.throwItemSpec().material()).isEqualTo(throwItemMaterial);
        assertThat(spec.throwItemSpec().displayName()).isEqualTo(throwItemDisplayName);
        assertThat(spec.throwItemSpec().damage()).isEqualTo(throwItemDamage);

        assertThat(spec.controlsSpec().throwAction()).isEqualTo(throwAction);
        assertThat(spec.controlsSpec().cookAction()).isEqualTo(cookAction);
        assertThat(spec.controlsSpec().placeAction()).isEqualTo(placeAction);
        assertThat(spec.controlsSpec().activateAction()).isEqualTo(activateAction);

        assertThat(spec.deploySpec().health()).isEqualTo(health);
        assertThat(spec.deploySpec().throwPropertiesSpec().throwSounds()).isNull();
        assertThat(spec.deploySpec().throwPropertiesSpec().velocity()).isEqualTo(throwVelocity);
        assertThat(spec.deploySpec().throwPropertiesSpec().cooldown()).isEqualTo(throwCooldown);
        assertThat(spec.deploySpec().cookPropertiesSpec().cookSounds()).isNull();
        assertThat(spec.deploySpec().placePropertiesSpec().material()).isEqualTo(placeMaterial);
        assertThat(spec.deploySpec().placePropertiesSpec().placeSounds()).isNull();
        assertThat(spec.deploySpec().placePropertiesSpec().cooldown()).isEqualTo(placeCooldown);
    }
}
