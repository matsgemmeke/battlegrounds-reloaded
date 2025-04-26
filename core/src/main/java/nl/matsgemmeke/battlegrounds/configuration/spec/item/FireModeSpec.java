package nl.matsgemmeke.battlegrounds.configuration.spec.item;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the immutable, validated configuration for a fire mode loaded from a YAML file.
 *
 * @param type the fire mode type
 * @param amountOfShots the amount of shots produced in a fire burst, will only be non-null when type equals to {@code BURST_MODE}
 * @param rateOfFire the rate of fire measured in rounds per minute, will only be non-null when type equals to {@code BURST_MODE} or {@code FULLY_AUTOMATIC}
 * @param delayBetweenShots the duration in ticks between fired shots, will only be non-null when type equals to {@code SEMI_AUTOMATIC}
 */
public record FireModeSpec(
        @NotNull String type,
        @Nullable Integer amountOfShots,
        @Nullable Integer rateOfFire,
        @Nullable Long delayBetweenShots
) { }
