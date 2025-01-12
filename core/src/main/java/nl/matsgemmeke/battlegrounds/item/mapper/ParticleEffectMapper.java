package nl.matsgemmeke.battlegrounds.item.mapper;

import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ParticleEffectMapper {

    /**
     * Maps the values from a configuration section to a {@link ParticleEffect}. Throws a
     * {@link MappingException} if the mapper cannot create a {@link ParticleEffect} instance using the given values.
     *
     * @param values the values in a map
     * @return a particle effect instance
     */
    @NotNull
    public ParticleEffect map(@NotNull Map<String, Object> values) {
        Particle particle;
        String particleValue = (String) values.get("particle");

        if (particleValue == null) {
            throw new MappingException("Cannot map particle effect without particle type");
        }

        try {
            particle = Particle.valueOf(particleValue);
        } catch (IllegalArgumentException e) {
            throw new MappingException("Cannot map particle effect because " + particleValue + " is not a valid particle type");
        }

        int count = Integer.parseInt(values.get("count").toString());
        double offsetX = Double.parseDouble(values.get("offset-x").toString());
        double offsetY = Double.parseDouble(values.get("offset-y").toString());
        double offsetZ = Double.parseDouble(values.get("offset-z").toString());
        double extra = Double.parseDouble(values.get("extra").toString());
        Material blockDataMaterial = null;

        if (values.containsKey("block-data")) {
            String blockDataMaterialValue = (String) values.get("block-data");

            try {
                blockDataMaterial = Material.valueOf(blockDataMaterialValue);
            } catch (IllegalArgumentException e) {
                throw new MappingException("Cannot map particle effect because " + blockDataMaterialValue + " is not a valid material");
            }
        }

        return new ParticleEffect(particle, count, offsetX, offsetY, offsetZ, extra, blockDataMaterial);
    }
}
