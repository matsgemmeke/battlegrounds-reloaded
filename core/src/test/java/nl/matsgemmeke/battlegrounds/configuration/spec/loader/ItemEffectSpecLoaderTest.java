package nl.matsgemmeke.battlegrounds.configuration.spec.loader;

import nl.matsgemmeke.battlegrounds.configuration.YamlReader;
import nl.matsgemmeke.battlegrounds.configuration.item.particle.ParticleEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.particle.ParticleEffectSpecLoader;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.PotionEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.RangeProfileSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.effect.ActivationPatternSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.effect.ItemEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.effect.TriggerSpec;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemEffectSpecLoaderTest {

    private static final String BASE_ROUTE = "base-route";

    @Test
    public void loadSpecReturnsItemEffectSpecContainingValidatedValues() {
        String type = "COMBUSTION";
        Set<String> triggerRoutes = Set.of("activator");
        TriggerSpec triggerSpec = new TriggerSpec("TIMED", 20L, null, null, null);
        RangeProfileSpec rangeProfileSpec = new RangeProfileSpec(1.0, 1.0, 1.0, 1.0, 1.0, 1.0);

        Double minSize = 1.0;
        Double maxSize = 2.0;
        Double density = 5.0;
        Double growth = 0.1;
        Long growthInterval = 1L;
        Long minDuration = 100L;
        Long maxDuration = 200L;

        Float power = 2.0f;
        Boolean damageBlocks = true;
        Boolean spreadFire = false;

        ParticleEffectSpec particleEffectSpec = new ParticleEffectSpec("BLOCK_CRACK", 10, 0.1, 0.2, 0.3, 0.0, "STONE", null);
        PotionEffectSpec potionEffectSpec = new PotionEffectSpec("BLINDNESS", 100, 1, true, false, true);
        ActivationPatternSpec activationPatternSpec = new ActivationPatternSpec(2L, 100L, 200L, 10L, 20L);

        YamlReader yamlReader = mock(YamlReader.class);
        when(yamlReader.getString("base-route.type")).thenReturn(type);
        when(yamlReader.getRoutes("base-route.triggers")).thenReturn(triggerRoutes);
        when(yamlReader.contains("base-route.range")).thenReturn(true);

        when(yamlReader.getOptionalDouble("base-route.min-size")).thenReturn(Optional.of(minSize));
        when(yamlReader.getOptionalDouble("base-route.max-size")).thenReturn(Optional.of(maxSize));
        when(yamlReader.getOptionalDouble("base-route.density")).thenReturn(Optional.of(density));
        when(yamlReader.getOptionalDouble("base-route.growth")).thenReturn(Optional.of(growth));
        when(yamlReader.getOptionalLong("base-route.growth-interval")).thenReturn(Optional.of(growthInterval));
        when(yamlReader.getOptionalLong("base-route.min-duration")).thenReturn(Optional.of(minDuration));
        when(yamlReader.getOptionalLong("base-route.max-duration")).thenReturn(Optional.of(maxDuration));
        when(yamlReader.getString("base-route.activation-sounds")).thenReturn(null);

        when(yamlReader.getOptionalFloat("base-route.power")).thenReturn(Optional.of(power));
        when(yamlReader.getOptionalBoolean("base-route.damage-blocks")).thenReturn(Optional.of(damageBlocks));
        when(yamlReader.getOptionalBoolean("base-route.spread-fire")).thenReturn(Optional.of(spreadFire));

        TriggerSpecLoader triggerSpecLoader = mock(TriggerSpecLoader.class);
        when(triggerSpecLoader.loadSpec("base-route.triggers.activator")).thenReturn(triggerSpec);

        RangeProfileSpecLoader rangeProfileSpecLoader = mock(RangeProfileSpecLoader.class);
        when(rangeProfileSpecLoader.loadSpec("base-route.range")).thenReturn(rangeProfileSpec);

        ParticleEffectSpecLoader particleEffectSpecLoader = mock(ParticleEffectSpecLoader.class);
        when(particleEffectSpecLoader.loadSpec("base-route.particle-effect")).thenReturn(particleEffectSpec);

        PotionEffectSpecLoader potionEffectSpecLoader = mock(PotionEffectSpecLoader.class);
        when(potionEffectSpecLoader.loadSpec("base-route.potion-effect")).thenReturn(potionEffectSpec);

        ActivationPatternSpecLoader activationPatternSpecLoader = mock(ActivationPatternSpecLoader.class);
        when(activationPatternSpecLoader.loadSpec("base-route.activation-pattern")).thenReturn(activationPatternSpec);

        ItemEffectSpecLoader specLoader = new ItemEffectSpecLoader(yamlReader, activationPatternSpecLoader, particleEffectSpecLoader, potionEffectSpecLoader, rangeProfileSpecLoader, triggerSpecLoader);
        ItemEffectSpec spec = specLoader.loadSpec(BASE_ROUTE);

        assertThat(spec.type()).isEqualTo(type);
        assertThat(spec.triggers()).containsExactly(triggerSpec);
        assertThat(spec.rangeProfile()).isEqualTo(rangeProfileSpec);

        assertThat(spec.minSize()).isEqualTo(minSize);
        assertThat(spec.maxSize()).isEqualTo(maxSize);
        assertThat(spec.density()).isEqualTo(density);
        assertThat(spec.growth()).isEqualTo(growth);
        assertThat(spec.growthInterval()).isEqualTo(growthInterval);
        assertThat(spec.minDuration()).isEqualTo(minDuration);
        assertThat(spec.maxDuration()).isEqualTo(maxDuration);
        assertThat(spec.activationSounds()).isNull();

        assertThat(spec.power()).isEqualTo(power);
        assertThat(spec.damageBlocks()).isEqualTo(damageBlocks);
        assertThat(spec.spreadFire()).isEqualTo(spreadFire);
    }
}
