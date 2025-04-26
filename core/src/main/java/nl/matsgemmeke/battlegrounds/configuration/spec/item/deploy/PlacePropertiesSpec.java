package nl.matsgemmeke.battlegrounds.configuration.spec.item.deploy;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the immutable, validated configuration for place deployment properties loaded from a YAML file.
 *
 * @param material the material value of the deployed block
 * @param placeSounds the raw value for the sounds produced when performing a place deployment
 * @param cooldown the length of the cooldown in ticks that disables the performer to perform other deployments
 */
public record PlacePropertiesSpec(
        @NotNull String material,
        @Nullable String placeSounds,
        @NotNull Long cooldown
) { }
