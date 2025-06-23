package nl.matsgemmeke.battlegrounds.configuration.spec.item;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the immutable, validated configuration for a spread pattern loaded from a YAML file.
 *
 * @param type             the type of spread pattern
 * @param projectileAmount the amount of projectiles that appear in the pattern, will only be non-null when type equals
 *                         to {@code BUCKSHOT}
 * @param horizontalSpread the amount of horizontal spread, will only be non-null when type equals to {@code BUCKSHOT}
 * @param verticalSpread   the amount of vertical spread, will only be non-null when type equals to {@code BUCKSHOT}
 */
public record SpreadPatternSpec(
        @NotNull String type,
        @Nullable Integer projectileAmount,
        @Nullable Float horizontalSpread,
        @Nullable Float verticalSpread
) { }
