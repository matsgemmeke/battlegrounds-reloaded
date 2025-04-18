package nl.matsgemmeke.battlegrounds.configuration.spec.item;

import org.jetbrains.annotations.NotNull;

public record ItemStackSpec(@NotNull String material, @NotNull String displayName, @NotNull Integer damage) { }
