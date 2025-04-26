package nl.matsgemmeke.battlegrounds.configuration.spec.item;

import org.jetbrains.annotations.NotNull;

/**
 * Represents the immutable, validated configuration for an item stack loaded from a YAML file.
 *
 * @param material the item stack material value
 * @param displayName the item stack display name
 * @param damage the damage applied to the item stack
 */
public record ItemStackSpec(@NotNull String material, @NotNull String displayName, @NotNull Integer damage) { }
