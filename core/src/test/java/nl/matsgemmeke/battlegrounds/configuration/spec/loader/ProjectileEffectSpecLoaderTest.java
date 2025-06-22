package nl.matsgemmeke.battlegrounds.configuration.spec.loader;

import nl.matsgemmeke.battlegrounds.configuration.YamlReader;
import nl.matsgemmeke.battlegrounds.configuration.item.particle.ParticleEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.particle.ParticleEffectSpecLoader;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.deploy.ProjectileEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.effect.TriggerSpec;
import org.junit.jupiter.api.Test;

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
        Double horizontalFriction = 3.0;
        Double verticalFriction = 2.0;
        Integer maxActivations = 3;
        ParticleEffectSpec particleEffectSpec = new ParticleEffectSpec("FLAME", 1, 0.0, 0.0, 0.0, 0.0, null, null);
        Set<String> triggerRoutes = Set.of("floor-hit");
        TriggerSpec triggerSpec = new TriggerSpec("FLOOR_HIT", 5L, 1L, null, null);

        YamlReader yamlReader = mock(YamlReader.class);
        when(yamlReader.getString("base-route.type")).thenReturn(type);
        when(yamlReader.getString("base-route.sounds")).thenReturn(null);
        when(yamlReader.getOptionalDouble("base-route.horizontal-friction")).thenReturn(Optional.of(horizontalFriction));
        when(yamlReader.getOptionalDouble("base-route.vertical-friction")).thenReturn(Optional.of(verticalFriction));
        when(yamlReader.getOptionalInt("base-route.max-activations")).thenReturn(Optional.of(maxActivations));
        when(yamlReader.getRoutes("base-route.triggers")).thenReturn(triggerRoutes);
        when(yamlReader.contains("base-route.particle-effect")).thenReturn(true);

        ParticleEffectSpecLoader particleEffectSpecLoader = mock(ParticleEffectSpecLoader.class);
        when(particleEffectSpecLoader.loadSpec("base-route.particle-effect")).thenReturn(particleEffectSpec);

        TriggerSpecLoader triggerSpecLoader = mock(TriggerSpecLoader.class);
        when(triggerSpecLoader.loadSpec("base-route.triggers.floor-hit")).thenReturn(triggerSpec);

        ProjectileEffectSpecLoader specLoader = new ProjectileEffectSpecLoader(yamlReader, particleEffectSpecLoader, triggerSpecLoader);
        ProjectileEffectSpec spec = specLoader.loadSpec(BASE_ROUTE);

        assertThat(spec.type()).isEqualTo(type);
        assertThat(spec.sounds()).isNull();
        assertThat(spec.horizontalFriction()).isEqualTo(horizontalFriction);
        assertThat(spec.verticalFriction()).isEqualTo(verticalFriction);
        assertThat(spec.maxActivations()).isEqualTo(maxActivations);
        assertThat(spec.particleEffect()).isEqualTo(particleEffectSpec);
        assertThat(spec.triggers()).containsExactly(triggerSpec);
    }
}
