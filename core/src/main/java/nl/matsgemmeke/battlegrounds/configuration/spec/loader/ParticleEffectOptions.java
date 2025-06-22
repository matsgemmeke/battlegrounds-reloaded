package nl.matsgemmeke.battlegrounds.configuration.spec.loader;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public class ParticleEffectOptions {

    private static final Map<String, Set<ParticleEffectOptionType>> SUPPORTED_OPTIONS = Map.of(
            "BLOCK_CRACK", EnumSet.of(ParticleEffectOptionType.BLOCK_DATA),
            "REDSTONE", EnumSet.of(ParticleEffectOptionType.DUST_OPTIONS)
    );

    public static boolean isOptionSupported(String particle, ParticleEffectOptionType optionType) {
        return SUPPORTED_OPTIONS.getOrDefault(particle, Collections.emptySet()).contains(optionType);
    }
}
