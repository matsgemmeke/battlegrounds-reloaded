package nl.matsgemmeke.battlegrounds.configuration.spec.item.deploy;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ThrowPropertiesSpec(
        @Nullable String throwSounds,
        @NotNull Double velocity,
        @NotNull Long cooldown
) { }
