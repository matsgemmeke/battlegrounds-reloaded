package nl.matsgemmeke.battlegrounds.item.mapper.particle;

import org.bukkit.Particle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ParticleEffectOptionsTest {

    @Test
    @DisplayName("isOptionSupported returns true when given particle can be used with given ParticleEffectOptionType")
    void isOptionSupported_givenOptionSupported() {
        boolean supported = ParticleEffectOptions.isOptionSupported(Particle.DUST, ParticleEffectOptionType.DUST_OPTIONS);

        assertThat(supported).isTrue();
    }

    @Test
    @DisplayName("isOptionSupported returns when given particle cannot be used with given ParticleEffectOptionType")
    void isOptionSupported_givenOptionNotSupported() {
        boolean supported = ParticleEffectOptions.isOptionSupported(Particle.FLAME, ParticleEffectOptionType.BLOCK_DATA);

        assertThat(supported).isFalse();
    }
}
