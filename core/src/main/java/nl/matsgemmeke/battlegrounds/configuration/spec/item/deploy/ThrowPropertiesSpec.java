package nl.matsgemmeke.battlegrounds.configuration.spec.item.deploy;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the immutable, validated configuration for throw deployment properties loaded from a YAML file.
 *
 * @param throwSounds the raw value that defines the sounds produced when performing a throw deployment
 * @param velocity the initial velocity applied to the projectile when performing a throw deployment
 * @param cooldown the length of the cooldown in ticks that disables the performer to perform other deployments
 */
public record ThrowPropertiesSpec(
        @Nullable String throwSounds,
        @NotNull Double velocity,
        @NotNull Long cooldown
) { }
