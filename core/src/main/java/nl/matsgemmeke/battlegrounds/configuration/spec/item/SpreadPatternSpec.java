package nl.matsgemmeke.battlegrounds.configuration.spec.item;

import org.jetbrains.annotations.NotNull;

/**
 * Represents the immutable, validated configuration for a spread pattern loaded from a YAML file.
 *
 * @param type the type of spread pattern
 * @param projectileAmount the amount of projectiles that appear in the pattern
 * @param horizontalSpread the amount of horizontal spread
 * @param verticalSpread the amount of vertical spread
 */
public record SpreadPatternSpec(
        @NotNull String type,
        @NotNull Integer projectileAmount,
        @NotNull Float horizontalSpread,
        @NotNull Float verticalSpread
) { }
