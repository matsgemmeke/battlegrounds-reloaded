package nl.matsgemmeke.battlegrounds.configuration.spec.item;

import org.jetbrains.annotations.NotNull;

/**
 * Represents the immutable, validated configuration for a potion effect.
 *
 * @param type      the potion effect type value
 * @param duration  the potion effect duration
 * @param amplifier the potion effect amplifier
 * @param ambient   whether the potion effect is ambient
 * @param particles whether the potion effect displays particles
 * @param icon      whether the potion effect shows an icon
 */
public record PotionEffectSpec(
        @NotNull String type,
        @NotNull Integer duration,
        @NotNull Integer amplifier,
        @NotNull Boolean ambient,
        @NotNull Boolean particles,
        @NotNull Boolean icon
) { }
