package nl.matsgemmeke.battlegrounds.configuration.item.shoot;

import nl.matsgemmeke.battlegrounds.configuration.YamlReader;
import nl.matsgemmeke.battlegrounds.configuration.item.particle.ParticleEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.particle.ParticleEffectSpecLoader;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ShootingSpecLoaderTest {

    private static final String BASE_ROUTE = "base-route";

    @Test
    public void createSpecReturnsShootingSpecContainingValidatedValues() {
        String fireModeType = "FULLY_AUTOMATIC";
        int rateOfFire = 600;
        ParticleEffectSpec trajectoryParticleEffectSpec = new ParticleEffectSpec("FLAME", 1, 0.0, 0.0, 0.0, 0.0, null, null);
        SpreadPatternSpec spreadPatternSpec = new SpreadPatternSpec("BUCKSHOT", 5, 0.5f, 0.5f);

        YamlReader yamlReader = mock(YamlReader.class);
        when(yamlReader.getString("base-route.shot-sounds")).thenReturn(null);
        when(yamlReader.getString("base-route.fire-mode.type")).thenReturn(fireModeType);
        when(yamlReader.getOptionalInt("base-route.fire-mode.amount-of-shots")).thenReturn(Optional.empty());
        when(yamlReader.getOptionalInt("base-route.fire-mode.cycle-cooldown")).thenReturn(Optional.empty());
        when(yamlReader.getOptionalInt("base-route.fire-mode.rate-of-fire")).thenReturn(Optional.of(rateOfFire));

        ParticleEffectSpecLoader particleEffectSpecLoader = mock(ParticleEffectSpecLoader.class);
        when(particleEffectSpecLoader.loadSpec("base-route.projectile.trajectory-particle-effect")).thenReturn(trajectoryParticleEffectSpec);

        SpreadPatternSpecLoader spreadPatternSpecLoader = mock(SpreadPatternSpecLoader.class);
        when(spreadPatternSpecLoader.loadSpec("base-route.spread-pattern")).thenReturn(spreadPatternSpec);

        ShootingSpecLoader shootingSpecLoader = new ShootingSpecLoader(yamlReader, particleEffectSpecLoader, spreadPatternSpecLoader);
        ShootingSpec spec = shootingSpecLoader.loadSpec(BASE_ROUTE);

        assertThat(spec.shotSounds()).isNull();
        assertThat(spec.fireMode().type()).isEqualTo(fireModeType);
        assertThat(spec.fireMode().amountOfShots()).isNull();
        assertThat(spec.fireMode().cycleCooldown()).isNull();
        assertThat(spec.fireMode().rateOfFire()).isEqualTo(rateOfFire);
        assertThat(spec.projectile().trajectoryParticleEffect()).isEqualTo(trajectoryParticleEffectSpec);
        assertThat(spec.spreadPattern()).isEqualTo(spreadPatternSpec);
    }
}
