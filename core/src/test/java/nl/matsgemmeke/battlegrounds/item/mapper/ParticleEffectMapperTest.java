package nl.matsgemmeke.battlegrounds.item.mapper;

import nl.matsgemmeke.battlegrounds.configuration.spec.item.ParticleEffectSpec;
import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ParticleEffectMapperTest {

    @Test
    public void mapReturnsParticleEffectInstanceWithBlockData() {
        int count = 10;
        double offsetX = 0.1;
        double offsetY = 0.2;
        double offsetZ = 0.3;
        double extra = 0.0;
        ParticleEffectSpec particleEffectSpec = new ParticleEffectSpec("BLOCK_CRACK", count, offsetX, offsetY, offsetZ, extra, "STONE");

        ParticleEffectMapper mapper = new ParticleEffectMapper();
        ParticleEffect particleEffect = mapper.map(particleEffectSpec);

        assertThat(particleEffect.particle()).isEqualTo(Particle.BLOCK_CRACK);
        assertThat(particleEffect.count()).isEqualTo(count);
        assertThat(particleEffect.offsetX()).isEqualTo(offsetX);
        assertThat(particleEffect.offsetY()).isEqualTo(offsetY);
        assertThat(particleEffect.offsetZ()).isEqualTo(offsetZ);
        assertThat(particleEffect.extra()).isEqualTo(extra);
        assertThat(particleEffect.blockDataMaterial()).isEqualTo(Material.STONE);
    }

    @Test
    public void mapReturnsParticleEffectInstanceWithoutBlockData() {
        int count = 10;
        double offsetX = 0.1;
        double offsetY = 0.2;
        double offsetZ = 0.3;
        double extra = 0.0;
        ParticleEffectSpec particleEffectSpec = new ParticleEffectSpec("FLAME", count, offsetX, offsetY, offsetZ, extra, null);

        ParticleEffectMapper mapper = new ParticleEffectMapper();
        ParticleEffect particleEffect = mapper.map(particleEffectSpec);

        assertThat(particleEffect.particle()).isEqualTo(Particle.FLAME);
        assertThat(particleEffect.count()).isEqualTo(count);
        assertThat(particleEffect.offsetX()).isEqualTo(offsetX);
        assertThat(particleEffect.offsetY()).isEqualTo(offsetY);
        assertThat(particleEffect.offsetZ()).isEqualTo(offsetZ);
        assertThat(particleEffect.extra()).isEqualTo(extra);
        assertThat(particleEffect.blockDataMaterial()).isNull();
    }
}
