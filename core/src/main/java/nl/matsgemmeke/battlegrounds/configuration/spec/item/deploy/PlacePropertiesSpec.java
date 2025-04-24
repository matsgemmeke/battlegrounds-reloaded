package nl.matsgemmeke.battlegrounds.configuration.spec.item.deploy;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record PlacePropertiesSpec(
        @NotNull String material,
        @Nullable String placeSounds,
        @NotNull Long cooldown
) { }
