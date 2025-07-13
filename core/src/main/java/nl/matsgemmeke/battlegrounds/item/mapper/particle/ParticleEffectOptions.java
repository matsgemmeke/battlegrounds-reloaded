package nl.matsgemmeke.battlegrounds.item.mapper.particle;

import org.bukkit.Particle;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public class ParticleEffectOptions {

    private static final Map<Particle, Set<ParticleEffectOptionType>> SUPPORTED_OPTIONS = Map.of(
            Particle.BLOCK_CRACK, EnumSet.of(ParticleEffectOptionType.BLOCK_DATA),
            Particle.REDSTONE, EnumSet.of(ParticleEffectOptionType.DUST_OPTIONS)
    );

    public static boolean isOptionSupported(Particle particle, ParticleEffectOptionType optionType) {
        return SUPPORTED_OPTIONS.getOrDefault(particle, Collections.emptySet()).contains(optionType);
    }
}
