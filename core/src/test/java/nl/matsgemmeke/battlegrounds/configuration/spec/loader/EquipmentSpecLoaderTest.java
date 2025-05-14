package nl.matsgemmeke.battlegrounds.configuration.spec.loader;

import nl.matsgemmeke.battlegrounds.configuration.YamlReader;
import nl.matsgemmeke.battlegrounds.configuration.spec.equipment.EquipmentSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.ParticleEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.PotionEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.RangeProfileSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.effect.ActivationPatternSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.effect.ItemEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.effect.TriggerSpec;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EquipmentSpecLoaderTest {

    @Test
    public void createSpecReturnsEquipmentSpecContainingValuesFromYamlReader() {
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
        ParticleEffectSpec destroyEffect = new ParticleEffectSpec("BLOCK_CRACK", 10, 0.1, 0.2, 0.3, 0.0, "STONE");

        Double throwVelocity = 2.0;
        Long throwCooldown = 20L;
        String placeMaterial = "STICK";
        Long placeCooldown = 40L;

        Long manualActivationDelay = 5L;
        String manualActivationSounds = "UI_BUTTON_CLICK-1-1-0";

        TriggerSpec triggerSpec = new TriggerSpec("TIMED", 3.0, 5L, 20L);
        RangeProfileSpec rangeProfileSpec = new RangeProfileSpec(35.0, 10.0, 25.0, 20.0, 15.0, 30.0);
        ParticleEffectSpec particleEffectSpec = new ParticleEffectSpec("FLAME", 10, 0.1, 0.2, 0.3, 0.0, null);
        PotionEffectSpec potionEffectSpec = new PotionEffectSpec("BLINDNESS", 100, 1, true, false, true);
        ActivationPatternSpec activationPatternSpec = new ActivationPatternSpec(2L, 200L, 100L, 20L, 10L);
        ItemEffectSpec effectSpec = new ItemEffectSpec("EXPLOSION", List.of(triggerSpec), rangeProfileSpec, 5.0, 1.0, 3.0, 0.5, 2L, 200L, 100L, null, 2.0f, true, false, particleEffectSpec, potionEffectSpec, activationPatternSpec);

        String throwAction = "LEFT_CLICK";
        String cookAction = "RIGHT_CLICK";
        String placeAction = "RIGHT_CLICK";
        String activateAction = "RIGHT_CLICK";

        YamlReader yamlReader = mock(YamlReader.class);
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

        when(yamlReader.contains("deploy.manual-activation")).thenReturn(true);
        when(yamlReader.getOptionalLong("deploy.manual-activation.delay")).thenReturn(Optional.of(manualActivationDelay));
        when(yamlReader.getString("deploy.manual-activation.sounds")).thenReturn(manualActivationSounds);

        when(yamlReader.contains("deploy.throwing")).thenReturn(true);
        when(yamlReader.getString("deploy.throwing.throw-sounds")).thenReturn(null);
        when(yamlReader.getOptionalDouble("deploy.throwing.velocity")).thenReturn(Optional.of(throwVelocity));
        when(yamlReader.getOptionalLong("deploy.throwing.cooldown")).thenReturn(Optional.of(throwCooldown));

        when(yamlReader.getString("deploy.throwing.cook-sounds")).thenReturn(null);

        when(yamlReader.contains("deploy.placing")).thenReturn(true);
        when(yamlReader.getString("deploy.placing.material")).thenReturn(placeMaterial);
        when(yamlReader.getString("deploy.placing.place-sounds")).thenReturn(null);
        when(yamlReader.getOptionalLong("deploy.placing.cooldown")).thenReturn(Optional.of(placeCooldown));

        ParticleEffectSpecLoader particleEffectSpecLoader = mock(ParticleEffectSpecLoader.class);
        when(particleEffectSpecLoader.loadSpec("deploy.on-destroy.particle-effect")).thenReturn(destroyEffect);

        ItemEffectSpecLoader itemEffectSpecLoader = mock(ItemEffectSpecLoader.class);
        when(itemEffectSpecLoader.loadSpec("effect")).thenReturn(effectSpec);

        EquipmentSpecLoader specLoader = new EquipmentSpecLoader(yamlReader, itemEffectSpecLoader, particleEffectSpecLoader);
        EquipmentSpec spec = specLoader.loadSpec();

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
        assertThat(spec.deployment().destroyEffect()).isEqualTo(destroyEffect);

        assertThat(spec.deployment().throwProperties()).isNotNull();
        assertThat(spec.deployment().throwProperties().throwSounds()).isNull();
        assertThat(spec.deployment().throwProperties().velocity()).isEqualTo(throwVelocity);
        assertThat(spec.deployment().throwProperties().cooldown()).isEqualTo(throwCooldown);

        assertThat(spec.deployment().cookProperties()).isNotNull();
        assertThat(spec.deployment().cookProperties().cookSounds()).isNull();

        assertThat(spec.deployment().placeProperties()).isNotNull();
        assertThat(spec.deployment().placeProperties().material()).isEqualTo(placeMaterial);
        assertThat(spec.deployment().placeProperties().placeSounds()).isNull();
        assertThat(spec.deployment().placeProperties().cooldown()).isEqualTo(placeCooldown);

        assertThat(spec.deployment().manualActivation()).isNotNull();
        assertThat(spec.deployment().manualActivation().delay()).isEqualTo(manualActivationDelay);
        assertThat(spec.deployment().manualActivation().sounds()).isEqualTo(manualActivationSounds);

        assertThat(spec.effect()).isEqualTo(effectSpec);
    }
}
