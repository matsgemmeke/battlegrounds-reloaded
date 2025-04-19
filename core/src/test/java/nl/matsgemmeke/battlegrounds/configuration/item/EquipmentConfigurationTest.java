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

        String itemMaterial = "SHEARS";
        String itemDisplayName = "Test Equipment";
        Integer itemDamage = 1;

        String activatorItemMaterial = "STICK";
        String activatorItemDisplayName = "Test Activator";
        Integer activatorItemDamage = 2;

        String throwItemMaterial = "STONE";
        String throwItemDisplayName = "Test Throw Item";
        Integer throwItemDamage = 3;

        String throwAction = "LEFT_CLICK";
        String cookAction = "RIGHT_CLICK";
        String placeAction = "RIGHT_CLICK";
        String activateAction = "RIGHT_CLICK";

        when(yamlReader.getString("name")).thenReturn(name);
        when(yamlReader.getString("description")).thenReturn(null);

        when(yamlReader.getString("item.material")).thenReturn(itemMaterial);
        when(yamlReader.getString("item.display-name")).thenReturn(itemDisplayName);
        when(yamlReader.getOptionalInt("item.damage")).thenReturn(Optional.of(itemDamage));

        when(yamlReader.contains("item.activator")).thenReturn(true);
        when(yamlReader.getString("item.activator.material")).thenReturn(activatorItemMaterial);
        when(yamlReader.getString("item.activator.display-name")).thenReturn(activatorItemDisplayName);
        when(yamlReader.getOptionalInt("item.activator.damage")).thenReturn(Optional.of(activatorItemDamage));

        when(yamlReader.contains("item.throw-item")).thenReturn(true);
        when(yamlReader.getString("item.throw-item.material")).thenReturn(throwItemMaterial);
        when(yamlReader.getString("item.throw-item.display-name")).thenReturn(throwItemDisplayName);
        when(yamlReader.getOptionalInt("item.throw-item.damage")).thenReturn(Optional.of(throwItemDamage));

        when(yamlReader.getString("controls.throw")).thenReturn(throwAction);
        when(yamlReader.getString("controls.cook")).thenReturn(cookAction);
        when(yamlReader.getString("controls.place")).thenReturn(placeAction);
        when(yamlReader.getString("controls.activate")).thenReturn(activateAction);

        EquipmentConfiguration configuration = new EquipmentConfiguration(yamlReader);
        EquipmentSpec spec = configuration.createSpec();

        assertThat(spec.name()).isEqualTo(name);
        assertThat(spec.description()).isNull();

        assertThat(spec.itemSpec().material()).isEqualTo(itemMaterial);
        assertThat(spec.itemSpec().displayName()).isEqualTo(itemDisplayName);
        assertThat(spec.itemSpec().damage()).isEqualTo(itemDamage);

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
    }
}
