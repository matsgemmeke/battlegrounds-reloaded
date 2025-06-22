package nl.matsgemmeke.battlegrounds.configuration.item.particle;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ParticleEffectOptionsTest {

    @Test
    public void isOptionSupportedReturnsTrueWhenGivenParticleSupportsGivenParticleEffectOptionType() {
        boolean supported = ParticleEffectOptions.isOptionSupported("REDSTONE", ParticleEffectOptionType.DUST_OPTIONS);

        assertThat(supported).isTrue();
    }

    @Test
    public void isOptionSupportedReturnsFalseWhenGivenParticleDoesNotSupportGivenParticleEffectOptionType() {
        boolean supported = ParticleEffectOptions.isOptionSupported("FLAME", ParticleEffectOptionType.DUST_OPTIONS);

        assertThat(supported).isFalse();
    }
}
