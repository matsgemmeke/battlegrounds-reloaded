package nl.matsgemmeke.battlegrounds.configuration.spec.item.effect;

import nl.matsgemmeke.battlegrounds.configuration.spec.item.RangeProfileSpec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Represents the immutable, validated configuration for an item effect.
 *
 * @param type         the item effect type
 * @param triggers     the trigger specifications
 * @param rangeProfile the range profile specification, will only be non-null when type equals to {@code COMBUSTION} or
 *                     {@code EXPLOSION}
 * @param maxSize      the maximum size of the effect, will only be non-null when type equals to {@code COMBUSTION} or
 *                     {@code SMOKE_SCREEN}
 * @param minSize      the minimum size of the effect,will only be non-null when type equals to {@code SMOKE_SCREEN}
 */
public record ItemEffectSpec(
        @NotNull String type,
        @NotNull List<TriggerSpec> triggers,
        @Nullable RangeProfileSpec rangeProfile,
        @Nullable Double maxSize,
        @Nullable Double minSize
) { }
