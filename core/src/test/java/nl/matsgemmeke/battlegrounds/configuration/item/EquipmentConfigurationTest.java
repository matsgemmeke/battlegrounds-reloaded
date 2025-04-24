package nl.matsgemmeke.battlegrounds.configuration.item;

import nl.matsgemmeke.battlegrounds.configuration.YamlReader;
import nl.matsgemmeke.battlegrounds.configuration.spec.equipment.EquipmentSpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
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
        Boolean activateEffectOnDestroy = true;
        Boolean removeOnDestroy = false;
        Boolean resetEffectOnDestroy = false;
        Map<String, Object> resistances = Map.of("explosive-damage", 0.5);

        String destroyEffectParticle = "BLOCK_CRACK";
        Integer destroyEffectCount = 10;
        Double destroyEffectOffsetX = 0.1;
        Double destroyEffectOffsetY = 0.2;
        Double destroyEffectOffsetZ = 0.3;
        Double destroyEffectExtra = 0.0;
        String destroyEffectBlockData = "STONE";

        Double throwVelocity = 2.0;
        Long throwCooldown = 20L;
        String placeMaterial = "STICK";
        Long placeCooldown = 40L;

        Long activationDelay = 5L;
        String activationSounds = "UI_BUTTON_CLICK-1-1-0";

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
        when(yamlReader.getOptionalBoolean("deploy.on-destroy.activate")).thenReturn(Optional.of(activateEffectOnDestroy));
        when(yamlReader.getOptionalBoolean("deploy.on-destroy.remove")).thenReturn(Optional.of(removeOnDestroy));
        when(yamlReader.getOptionalBoolean("deploy.on-destroy.reset")).thenReturn(Optional.of(resetEffectOnDestroy));
        when(yamlReader.getStringRouteMappedValues("deploy.resistances", false)).thenReturn(resistances);

        when(yamlReader.contains("deploy.on-destroy.particle-effect")).thenReturn(true);
        when(yamlReader.getString("deploy.on-destroy.particle-effect.particle")).thenReturn(destroyEffectParticle);
        when(yamlReader.getOptionalInt("deploy.on-destroy.particle-effect.count")).thenReturn(Optional.of(destroyEffectCount));
        when(yamlReader.getOptionalDouble("deploy.on-destroy.particle-effect.offset-x")).thenReturn(Optional.of(destroyEffectOffsetX));
        when(yamlReader.getOptionalDouble("deploy.on-destroy.particle-effect.offset-y")).thenReturn(Optional.of(destroyEffectOffsetY));
        when(yamlReader.getOptionalDouble("deploy.on-destroy.particle-effect.offset-z")).thenReturn(Optional.of(destroyEffectOffsetZ));
        when(yamlReader.getOptionalDouble("deploy.on-destroy.particle-effect.extra")).thenReturn(Optional.of(destroyEffectExtra));
        when(yamlReader.getString("deploy.on-destroy.particle-effect.block-data")).thenReturn(destroyEffectBlockData);

        when(yamlReader.contains("deploy.manual-activation")).thenReturn(true);
        when(yamlReader.getOptionalLong("deploy.manual-activation.activation-delay")).thenReturn(Optional.of(activationDelay));
        when(yamlReader.getString("deploy.manual-activation.activation-sounds")).thenReturn(activationSounds);

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

        assertThat(spec.displayItem().material()).isEqualTo(displayItemMaterial);
        assertThat(spec.displayItem().displayName()).isEqualTo(displayItemDisplayName);
        assertThat(spec.displayItem().damage()).isEqualTo(displayItemDamage);

        assertThat(spec.activatorItem()).isNotNull();
        assertThat(spec.activatorItem().material()).isEqualTo(activatorItemMaterial);
        assertThat(spec.activatorItem().displayName()).isEqualTo(activatorItemDisplayName);
        assertThat(spec.activatorItem().damage()).isEqualTo(activatorItemDamage);

        assertThat(spec.throwItem()).isNotNull();
        assertThat(spec.throwItem().material()).isEqualTo(throwItemMaterial);
        assertThat(spec.throwItem().displayName()).isEqualTo(throwItemDisplayName);
        assertThat(spec.throwItem().damage()).isEqualTo(throwItemDamage);

        assertThat(spec.controls().throwAction()).isEqualTo(throwAction);
        assertThat(spec.controls().cookAction()).isEqualTo(cookAction);
        assertThat(spec.controls().placeAction()).isEqualTo(placeAction);
        assertThat(spec.controls().activateAction()).isEqualTo(activateAction);

        assertThat(spec.deployment().health()).isEqualTo(health);
        assertThat(spec.deployment().activateEffectOnDestroy()).isEqualTo(activateEffectOnDestroy);
        assertThat(spec.deployment().removeOnDestroy()).isEqualTo(removeOnDestroy);
        assertThat(spec.deployment().resetEffectOnDestroy()).isEqualTo(resetEffectOnDestroy);
        assertThat(spec.deployment().resistances()).hasSize(1).containsEntry("explosive-damage", 0.5);

        assertThat(spec.deployment().destroyEffect()).isNotNull();
        assertThat(spec.deployment().destroyEffect().particle()).isEqualTo(destroyEffectParticle);
        assertThat(spec.deployment().destroyEffect().count()).isEqualTo(destroyEffectCount);
        assertThat(spec.deployment().destroyEffect().offsetX()).isEqualTo(destroyEffectOffsetX);
        assertThat(spec.deployment().destroyEffect().offsetY()).isEqualTo(destroyEffectOffsetY);
        assertThat(spec.deployment().destroyEffect().offsetZ()).isEqualTo(destroyEffectOffsetZ);
        assertThat(spec.deployment().destroyEffect().extra()).isEqualTo(destroyEffectExtra);
        assertThat(spec.deployment().destroyEffect().blockData()).isEqualTo(destroyEffectBlockData);

        assertThat(spec.deployment().throwPropertiesSpec()).isNotNull();
        assertThat(spec.deployment().throwPropertiesSpec().throwSounds()).isNull();
        assertThat(spec.deployment().throwPropertiesSpec().velocity()).isEqualTo(throwVelocity);
        assertThat(spec.deployment().throwPropertiesSpec().cooldown()).isEqualTo(throwCooldown);

        assertThat(spec.deployment().cookPropertiesSpec()).isNotNull();
        assertThat(spec.deployment().cookPropertiesSpec().cookSounds()).isNull();

        assertThat(spec.deployment().placePropertiesSpec()).isNotNull();
        assertThat(spec.deployment().placePropertiesSpec().material()).isEqualTo(placeMaterial);
        assertThat(spec.deployment().placePropertiesSpec().placeSounds()).isNull();
        assertThat(spec.deployment().placePropertiesSpec().cooldown()).isEqualTo(placeCooldown);

        assertThat(spec.deployment().manualActivationSpec()).isNotNull();
        assertThat(spec.deployment().manualActivationSpec().activationDelay()).isEqualTo(activationDelay);
        assertThat(spec.deployment().manualActivationSpec().activationSounds()).isEqualTo(activationSounds);
    }
}
