package nl.matsgemmeke.battlegrounds.configuration.spec.item;

import org.jetbrains.annotations.NotNull;

public record SpreadPatternSpec(
        @NotNull String type,
        @NotNull Integer projectileAmount,
        @NotNull Float horizontalSpread,
        @NotNull Float verticalSpread
) { }
