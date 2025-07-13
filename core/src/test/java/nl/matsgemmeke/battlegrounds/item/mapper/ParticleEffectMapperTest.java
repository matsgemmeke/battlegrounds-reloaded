package nl.matsgemmeke.battlegrounds.item.mapper;

import nl.matsgemmeke.battlegrounds.configuration.item.DustOptionsSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.ParticleEffectSpec;
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
        ParticleEffectSpec spec = this.createParticleEffectSpec("BLOCK_CRACK", "STONE", null);

        ParticleEffectMapper mapper = new ParticleEffectMapper();
        ParticleEffect particleEffect = mapper.map(spec);

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
        DustOptionsSpec dustOptionsSpec = new DustOptionsSpec();
        dustOptionsSpec.color = "#ab1234";
        dustOptionsSpec.size = 1.0f;

        ParticleEffectSpec particleEffectSpec = this.createParticleEffectSpec("BLOCK_CRACK", null, dustOptionsSpec);

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
        ParticleEffectSpec particleEffectSpec = this.createParticleEffectSpec("FLAME", null, null);

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

    private ParticleEffectSpec createParticleEffectSpec(String particle, String blockData, DustOptionsSpec dustOptionsSpec) {
        ParticleEffectSpec spec = new ParticleEffectSpec();
        spec.particle = particle;
        spec.count = COUNT;
        spec.offsetX = OFFSET_X;
        spec.offsetY = OFFSET_Y;
        spec.offsetZ = OFFSET_Z;
        spec.extra = EXTRA;
        spec.blockData = blockData;
        spec.dustOptions = dustOptionsSpec;
        return spec;
    }
}
