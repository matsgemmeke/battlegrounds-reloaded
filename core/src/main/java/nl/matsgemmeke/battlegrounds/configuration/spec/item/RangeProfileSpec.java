package nl.matsgemmeke.battlegrounds.configuration.spec.item;

import org.jetbrains.annotations.NotNull;

/**
 * Represents the immutable, validated configuration for a range profile.
 * <p>
 * @param shortRangeDamage the amount of damage applied inside the short range
 * @param shortRangeDistance the maximum distance for the short range
 * @param mediumRangeDamage the amount of damage applied inside the medium range
 * @param mediumRangeDistance the maximum distance for the medium range
 * @param longRangeDamage the amount of damage applied inside the long range
 * @param longRangeDistance the maximum distance for the long range
 */
public record RangeProfileSpec(
        @NotNull Double shortRangeDamage,
        @NotNull Double shortRangeDistance,
        @NotNull Double mediumRangeDamage,
        @NotNull Double mediumRangeDistance,
        @NotNull Double longRangeDamage,
        @NotNull Double longRangeDistance
) { }
