package nl.matsgemmeke.battlegrounds.configuration.spec.item.effect;

import nl.matsgemmeke.battlegrounds.configuration.spec.item.RangeProfileSpec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the immutable, validated configuration for an item effect.
 *
 * @param type         the item effect type
 * @param rangeProfile the range profile specification, will only be non-null when type equals to {@code COMBUSTION} or
 *                     {@code EXPLOSION}
 */
public record ItemEffectSpec(
        @NotNull String type,
        @Nullable RangeProfileSpec rangeProfile
) { }
