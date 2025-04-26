package nl.matsgemmeke.battlegrounds.item.mapper;

import nl.matsgemmeke.battlegrounds.configuration.spec.item.ParticleEffectSpec;
import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;

public class ParticleEffectMapper {

    /**
     * Maps the values from a particle effect specification to a {@link ParticleEffect}.
     *
     * @param spec the particle effect specification
     * @return a particle effect instance
     */
    @NotNull
    public ParticleEffect map(@NotNull ParticleEffectSpec spec) {
        Particle particle = Particle.valueOf(spec.particle());

        Material blockDataMaterial = null;
        String blockDataMaterialValue = spec.blockData();

        if (blockDataMaterialValue != null) {
            blockDataMaterial = Material.valueOf(blockDataMaterialValue);
        }

        return new ParticleEffect(particle, spec.count(), spec.offsetX(), spec.offsetY(), spec.offsetZ(), spec.extra(), blockDataMaterial);
    }
}
