package nl.matsgemmeke.battlegrounds.configuration.spec.item;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ParticleEffectSpec(
        @NotNull String particle,
        @NotNull Integer count,
        @NotNull Double offsetX,
        @NotNull Double offsetY,
        @NotNull Double offsetZ,
        @Nullable Double extra,
        @Nullable String blockData
) { }
