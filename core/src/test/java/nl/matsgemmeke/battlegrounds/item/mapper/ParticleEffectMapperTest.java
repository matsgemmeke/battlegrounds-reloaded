package nl.matsgemmeke.battlegrounds.item.mapper;

import nl.matsgemmeke.battlegrounds.configuration.item.particle.ParticleEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.particle.DustOptionsSpec;
import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ParticleEffectMapperTest {

    private static final int COUNT = 1;
    private static final double OFFSET_X = 0.1;
    private static final double OFFSET_Y = 0.2;
    private static final double OFFSET_Z = 0.3;
    private static final double EXTRA = 0.0;

    @Test
    public void mapReturnsParticleEffectInstanceWithBlockData() {
        ParticleEffectSpec particleEffectSpec = new ParticleEffectSpec("BLOCK_CRACK", COUNT, OFFSET_X, OFFSET_Y, OFFSET_Z, EXTRA, "STONE", null);

        ParticleEffectMapper mapper = new ParticleEffectMapper();
        ParticleEffect particleEffect = mapper.map(particleEffectSpec);

        assertThat(particleEffect.particle()).isEqualTo(Particle.BLOCK_CRACK);
        assertThat(particleEffect.count()).isEqualTo(COUNT);
        assertThat(particleEffect.offsetX()).isEqualTo(OFFSET_X);
        assertThat(particleEffect.offsetY()).isEqualTo(OFFSET_Y);
        assertThat(particleEffect.offsetZ()).isEqualTo(OFFSET_Z);
        assertThat(particleEffect.extra()).isEqualTo(EXTRA);
        assertThat(particleEffect.blockDataMaterial()).isEqualTo(Material.STONE);
        assertThat(particleEffect.dustOptions()).isNull();
    }

    @Test
    public void mapReturnsParticleEffectInstanceWithDustOptions() {
        DustOptionsSpec dustOptionsSpec = new DustOptionsSpec("#ab1234", 1);
        ParticleEffectSpec particleEffectSpec = new ParticleEffectSpec("BLOCK_CRACK", COUNT, OFFSET_X, OFFSET_Y, OFFSET_Z, EXTRA, null, dustOptionsSpec);

        ParticleEffectMapper mapper = new ParticleEffectMapper();
        ParticleEffect particleEffect = mapper.map(particleEffectSpec);

        assertThat(particleEffect.particle()).isEqualTo(Particle.BLOCK_CRACK);
        assertThat(particleEffect.count()).isEqualTo(COUNT);
        assertThat(particleEffect.offsetX()).isEqualTo(OFFSET_X);
        assertThat(particleEffect.offsetY()).isEqualTo(OFFSET_Y);
        assertThat(particleEffect.offsetZ()).isEqualTo(OFFSET_Z);
        assertThat(particleEffect.extra()).isEqualTo(EXTRA);
        assertThat(particleEffect.blockDataMaterial()).isNull();
        assertThat(particleEffect.dustOptions()).isNotNull();
        assertThat(particleEffect.dustOptions().getColor().getRed()).isEqualTo(171);
        assertThat(particleEffect.dustOptions().getColor().getGreen()).isEqualTo(18);
        assertThat(particleEffect.dustOptions().getColor().getBlue()).isEqualTo(52);
    }

    @Test
    public void mapReturnsParticleEffectInstanceWithoutAdditionalData() {
        ParticleEffectSpec particleEffectSpec = new ParticleEffectSpec("FLAME", COUNT, OFFSET_X, OFFSET_Y, OFFSET_Z, EXTRA, null, null);

        ParticleEffectMapper mapper = new ParticleEffectMapper();
        ParticleEffect particleEffect = mapper.map(particleEffectSpec);

        assertThat(particleEffect.particle()).isEqualTo(Particle.FLAME);
        assertThat(particleEffect.count()).isEqualTo(COUNT);
        assertThat(particleEffect.offsetX()).isEqualTo(OFFSET_X);
        assertThat(particleEffect.offsetY()).isEqualTo(OFFSET_Y);
        assertThat(particleEffect.offsetZ()).isEqualTo(OFFSET_Z);
        assertThat(particleEffect.extra()).isEqualTo(EXTRA);
        assertThat(particleEffect.blockDataMaterial()).isNull();
        assertThat(particleEffect.dustOptions()).isNull();
    }
}
