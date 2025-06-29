package nl.matsgemmeke.battlegrounds.configuration.item.particle;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the immutable, validated configuration for a particle effect loaded from a YAML file.
 *
 * @param particle    the particle type value
 * @param count       the amount of particles that appear in the effect
 * @param offsetX     the maximum random offset on the X axis
 * @param offsetY     the maximum random offset on the Y axis
 * @param offsetZ     the maximum random offset on the Z axis
 * @param extra       the extra data for this particle, depends on the particle used (normally speed)
 * @param blockData   the data to use for the particle or null, the type of this depends on the particle type
 * @param dustOptions the optional dust options for the particle
 */
public record ParticleEffectSpec(
        @NotNull String particle,
        @NotNull Integer count,
        @NotNull Double offsetX,
        @NotNull Double offsetY,
        @NotNull Double offsetZ,
        @Nullable Double extra,
        @Nullable String blockData,
        @Nullable DustOptionsSpec dustOptions
) { }
