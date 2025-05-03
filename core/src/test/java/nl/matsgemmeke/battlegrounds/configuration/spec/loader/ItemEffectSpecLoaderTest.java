package nl.matsgemmeke.battlegrounds.configuration.spec.loader;

import nl.matsgemmeke.battlegrounds.configuration.YamlReader;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.RangeProfileSpec;
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
        TriggerSpec triggerSpec = new TriggerSpec("ACTIVATOR", null, null, null);
        RangeProfileSpec rangeProfileSpec = new RangeProfileSpec(1.0, 1.0, 1.0, 1.0, 1.0, 1.0);

        Double maxSize = 2.0;
        Double minSize = 1.0;
        Double density = 5.0;
        Double growth = 0.1;
        Long growthInterval = 1L;
        Long maxDuration = 200L;
        Long minDuration = 100L;

        Float power = 2.0f;
        Boolean damageBlocks = true;
        Boolean spreadFire = false;

        YamlReader yamlReader = mock(YamlReader.class);
        when(yamlReader.getString("base-route.type")).thenReturn(type);
        when(yamlReader.getRoutes("base-route.triggers")).thenReturn(triggerRoutes);

        when(yamlReader.getOptionalDouble("base-route.max-size")).thenReturn(Optional.of(maxSize));
        when(yamlReader.getOptionalDouble("base-route.min-size")).thenReturn(Optional.of(minSize));
        when(yamlReader.getOptionalDouble("base-route.density")).thenReturn(Optional.of(density));
        when(yamlReader.getOptionalDouble("base-route.growth")).thenReturn(Optional.of(growth));
        when(yamlReader.getOptionalLong("base-route.growth-interval")).thenReturn(Optional.of(growthInterval));
        when(yamlReader.getOptionalLong("base-route.max-duration")).thenReturn(Optional.of(maxDuration));
        when(yamlReader.getOptionalLong("base-route.min-duration")).thenReturn(Optional.of(minDuration));
        when(yamlReader.getString("base-route.activation-sounds")).thenReturn(null);

        when(yamlReader.getOptionalFloat("base-route.power")).thenReturn(Optional.of(power));
        when(yamlReader.getOptionalBoolean("base-route.damage-blocks")).thenReturn(Optional.of(damageBlocks));
        when(yamlReader.getOptionalBoolean("base-route.spread-fire")).thenReturn(Optional.of(spreadFire));

        TriggerSpecLoader triggerSpecLoader = mock(TriggerSpecLoader.class);
        when(triggerSpecLoader.loadSpec("base-route.triggers.activator")).thenReturn(triggerSpec);

        RangeProfileSpecLoader rangeProfileSpecLoader = mock(RangeProfileSpecLoader.class);
        when(rangeProfileSpecLoader.loadSpec("base-route.range")).thenReturn(rangeProfileSpec);

        ItemEffectSpecLoader specLoader = new ItemEffectSpecLoader(yamlReader, rangeProfileSpecLoader, triggerSpecLoader);
        ItemEffectSpec spec = specLoader.loadSpec(BASE_ROUTE);

        assertThat(spec.type()).isEqualTo(type);
        assertThat(spec.triggers()).containsExactly(triggerSpec);
        assertThat(spec.rangeProfile()).isEqualTo(rangeProfileSpec);

        assertThat(spec.maxSize()).isEqualTo(maxSize);
        assertThat(spec.minSize()).isEqualTo(minSize);
        assertThat(spec.density()).isEqualTo(density);
        assertThat(spec.growth()).isEqualTo(growth);
        assertThat(spec.growthInterval()).isEqualTo(growthInterval);
        assertThat(spec.maxDuration()).isEqualTo(maxDuration);
        assertThat(spec.minDuration()).isEqualTo(minDuration);
        assertThat(spec.activationSounds()).isNull();

        assertThat(spec.power()).isEqualTo(power);
        assertThat(spec.damageBlocks()).isEqualTo(damageBlocks);
        assertThat(spec.spreadFire()).isEqualTo(spreadFire);
    }
}
