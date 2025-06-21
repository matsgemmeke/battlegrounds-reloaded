package nl.matsgemmeke.battlegrounds.configuration.spec.item;

import org.jetbrains.annotations.NotNull;

/**
 * Represents the immutable, validated configuration for dust options.
 *
 * @param color the dust color
 * @param size  the dust size
 */
public record DustOptionsSpec(@NotNull String color, @NotNull Integer size) {
}
