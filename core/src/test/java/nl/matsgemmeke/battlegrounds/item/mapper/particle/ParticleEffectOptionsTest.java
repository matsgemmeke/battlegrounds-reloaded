package nl.matsgemmeke.battlegrounds.item.mapper.particle;

import org.bukkit.Particle;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ParticleEffectOptionsTest {

    @Test
    public void isOptionSupportedReturnsTrueWhenGivenParticleCanBeUsedWithGivenParticleEffectOptionType() {
        boolean supported = ParticleEffectOptions.isOptionSupported(Particle.REDSTONE, ParticleEffectOptionType.DUST_OPTIONS);

        assertThat(supported).isTrue();
    }

    @Test
    public void isOptionSupportedReturnsFalseWhenGivenParticleCannotBeUsedWithGivenParticleEffectOptionType() {
        boolean supported = ParticleEffectOptions.isOptionSupported(Particle.FLAME, ParticleEffectOptionType.BLOCK_DATA);

        assertThat(supported).isFalse();
    }
}
