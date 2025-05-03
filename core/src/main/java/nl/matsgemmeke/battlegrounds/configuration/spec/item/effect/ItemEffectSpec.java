package nl.matsgemmeke.battlegrounds.configuration.spec.item.effect;

import nl.matsgemmeke.battlegrounds.configuration.spec.item.ParticleEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.RangeProfileSpec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Represents the immutable, validated configuration for an item effect.
 *
 * @param type              the item effect type
 * @param triggers          the trigger specifications
 * @param rangeProfile      the range profile specification, will only be non-null when type equals {@code COMBUSTION}
 *                          or {@code EXPLOSION}
 * @param maxSize           the maximum size of the effect, will only be non-null when type equals {@code COMBUSTION}
 *                          or {@code SMOKE_SCREEN}
 * @param minSize           the minimum size of the effect, will only be non-null when type equals {@code SMOKE_SCREEN}
 * @param density           the density of the item, will only be non-null when type equals {@code SMOKE_SCREEN}
 * @param growth            the amount of growth per growth cycle, will only be non-null when type equals
 *                          {@code COMBUSTION} or {@code SMOKE_SCREEN}
 * @param growthInterval    the interval in ticks between growth cycles, will only be non-null when type equals
 *                          {@code COMBUSTION} or {@code SMOKE_SCREEN}
 * @param maxDuration       the maximum duration of the effect in ticks, will only be non-null when type equals
 *                          {@code COMBUSTION}, {@code GUN_FIRE_SIMULATION} or {@code SMOKE_SCREEN}
 * @param minDuration       the minimum duration of the effect in ticks, will only be non-null when type equals
 *                          {@code COMBUSTION}, {@code GUN_FIRE_SIMULATION} or {@code SMOKE_SCREEN}
 * @param activationSounds  the sounds the effect produces when activated
 * @param power             the power of the effect, will only be non-null when type equals {@code EXPLOSION} or
 *                          {@code FLASH}
 * @param damageBlocks      whether the effect will damage surrounding blocks, will only be non-null when type equals
 *                          {@code COMBUSTION}, {@code EXPLOSION} or {@code FLASH}
 * @param spreadFire        whether the effect will spread fire to surrounding blocks, will only be non-null when type
 *                          equals {@code COMBUSTION}, {@code EXPLOSION} or {@code FLASH}
 * @param particleEffect    the specification of the particle effect that is displayed during the effect, will only be
 *                          non-null when type equals {@code SMOKE_SCREEN}
 * @param activationPattern
 */
public record ItemEffectSpec(
        @NotNull String type,
        @NotNull List<TriggerSpec> triggers,
        @Nullable RangeProfileSpec rangeProfile,
        @Nullable Double maxSize,
        @Nullable Double minSize,
        @Nullable Double density,
        @Nullable Double growth,
        @Nullable Long growthInterval,
        @Nullable Long maxDuration,
        @Nullable Long minDuration,
        @Nullable String activationSounds,
        @Nullable Float power,
        @Nullable Boolean damageBlocks,
        @Nullable Boolean spreadFire,
        @Nullable ParticleEffectSpec particleEffect,
        @Nullable ActivationPatternSpec activationPattern
) { }
