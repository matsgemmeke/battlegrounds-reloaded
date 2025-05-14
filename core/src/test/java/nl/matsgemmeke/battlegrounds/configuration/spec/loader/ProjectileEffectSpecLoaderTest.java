package nl.matsgemmeke.battlegrounds.configuration.spec.loader;

import nl.matsgemmeke.battlegrounds.configuration.YamlReader;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.ParticleEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.deploy.ProjectileEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.effect.TriggerSpec;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProjectileEffectSpecLoaderTest {

    private static final String BASE_ROUTE = "base-route";

    @Test
    public void createSpecReturnsProjectileEffectSpecContainingValidatedValues() {
        String type = "BOUNCE";
        Long delay = 10L;
        List<Long> intervals = List.of(2L, 4L, 6L);
        Double horizontalFriction = 3.0;
        Double verticalFriction = 2.0;
        ParticleEffectSpec particleEffectSpec = new ParticleEffectSpec("FLAME", 1, 0.0, 0.0, 0.0, 0.0, null);
        Set<String> triggerRoutes = Set.of("floor-hit");
        TriggerSpec triggerSpec = new TriggerSpec("FLOOR_HIT", null, 1L, null);

        YamlReader yamlReader = mock(YamlReader.class);
        when(yamlReader.getString("base-route.type")).thenReturn(type);
        when(yamlReader.getOptionalLong("base-route.delay")).thenReturn(Optional.of(delay));
        when(yamlReader.getOptionalLongList("base-route.intervals")).thenReturn(Optional.of(intervals));
        when(yamlReader.getString("base-route.sounds")).thenReturn(null);
        when(yamlReader.getOptionalDouble("base-route.horizontal-friction")).thenReturn(Optional.of(horizontalFriction));
        when(yamlReader.getOptionalDouble("base-route.vertical-friction")).thenReturn(Optional.of(verticalFriction));
        when(yamlReader.getRoutes("base-route.triggers")).thenReturn(triggerRoutes);

        ParticleEffectSpecLoader particleEffectSpecLoader = mock(ParticleEffectSpecLoader.class);
        when(particleEffectSpecLoader.loadSpec("base-route.particle-effect")).thenReturn(particleEffectSpec);

        TriggerSpecLoader triggerSpecLoader = mock(TriggerSpecLoader.class);
        when(triggerSpecLoader.loadSpec("base-route.triggers.floor-hit")).thenReturn(triggerSpec);

        ProjectileEffectSpecLoader specLoader = new ProjectileEffectSpecLoader(yamlReader, particleEffectSpecLoader, triggerSpecLoader);
        ProjectileEffectSpec spec = specLoader.loadSpec(BASE_ROUTE);

        assertThat(spec.type()).isEqualTo(type);
        assertThat(spec.delay()).isEqualTo(delay);
        assertThat(spec.intervals()).isEqualTo(intervals);
        assertThat(spec.sounds()).isNull();
        assertThat(spec.horizontalFriction()).isEqualTo(horizontalFriction);
        assertThat(spec.verticalFriction()).isEqualTo(verticalFriction);
        assertThat(spec.particleEffect()).isEqualTo(particleEffectSpec);
        assertThat(spec.triggers()).containsExactly(triggerSpec);
    }
}
