package nl.matsgemmeke.battlegrounds.configuration.spec.item;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public record ItemStackSpecification(@NotNull Material material, @NotNull String displayName, @NotNull Integer damage) { }
