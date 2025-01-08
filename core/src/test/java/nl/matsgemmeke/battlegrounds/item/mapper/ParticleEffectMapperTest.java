package nl.matsgemmeke.battlegrounds.item.mapper;

import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ParticleEffectMapperTest {

    @Test
    public void mapThrowsExceptionIfGivenValuesHasNoParticleValue() {
        Map<String, Object> values = Collections.emptyMap();

        ParticleEffectMapper mapper = new ParticleEffectMapper();

        assertThrows(MappingException.class, () -> mapper.map(values));
    }

    @Test
    public void mapThrowsExceptionIfGivenValuesHasInvalidParticleValue() {
        Map<String, Object> values = Map.of("particle", "fail");

        ParticleEffectMapper mapper = new ParticleEffectMapper();

        assertThrows(MappingException.class, () -> mapper.map(values));
    }

    @Test
    public void mapReturnsParticleEffectInstanceWithBlockData() {
        Map<String, Object> values = Map.of(
                "particle", "BLOCK_CRACK",
                "count", 10,
                "offset-x", 0.1,
                "offset-y", 0.2,
                "offset-z", 0.3,
                "extra", 0.0,
                "block-data", "STONE"
        );

        ParticleEffectMapper mapper = new ParticleEffectMapper();
        ParticleEffect particleEffect = mapper.map(values);

        assertEquals(Particle.BLOCK_CRACK, particleEffect.particle());
        assertEquals(10, particleEffect.count());
        assertEquals(0.1, particleEffect.offsetX());
        assertEquals(0.2, particleEffect.offsetY());
        assertEquals(0.3, particleEffect.offsetZ());
        assertEquals(0.0, particleEffect.extra());
        assertEquals(Material.STONE, particleEffect.blockDataMaterial());
    }

    @Test
    public void mapThrowsExceptionIfGivenValuesHasInvalidBlockDataMaterialValue() {
        Map<String, Object> values = Map.of(
                "particle", "BLOCK_CRACK",
                "count", 10,
                "offset-x", 0.1,
                "offset-y", 0.2,
                "offset-z", 0.3,
                "extra", 0.0,
                "block-data", "fail"
        );

        ParticleEffectMapper mapper = new ParticleEffectMapper();

        assertThrows(MappingException.class, () -> mapper.map(values));
    }

    @Test
    public void mapReturnsParticleEffectInstanceWithoutBlockData() {
        Map<String, Object> values = Map.of(
                "particle", "FLAME",
                "count", 10,
                "offset-x", 0.1,
                "offset-y", 0.2,
                "offset-z", 0.3,
                "extra", 0.0
        );

        ParticleEffectMapper mapper = new ParticleEffectMapper();
        ParticleEffect particleEffect = mapper.map(values);

        assertEquals(Particle.FLAME, particleEffect.particle());
        assertEquals(10, particleEffect.count());
        assertEquals(0.1, particleEffect.offsetX());
        assertEquals(0.2, particleEffect.offsetY());
        assertEquals(0.3, particleEffect.offsetZ());
        assertEquals(0.0, particleEffect.extra());
        assertNull(particleEffect.blockDataMaterial());
    }
}
