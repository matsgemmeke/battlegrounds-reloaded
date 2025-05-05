package nl.matsgemmeke.battlegrounds.configuration.spec.item.effect;

import nl.matsgemmeke.battlegrounds.configuration.spec.item.ParticleEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.PotionEffectSpec;
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
 * @param minSize           the minimum size of the effect, will only be non-null when type equals {@code COMBUSTION}
 *                          or {@code SMOKE_SCREEN}
 * @param maxSize           the maximum size of the effect, will only be non-null when type equals {@code COMBUSTION},
 *                          {@code FLASH} or {@code SMOKE_SCREEN}
 * @param density           the density of the item, will only be non-null when type equals {@code SMOKE_SCREEN}
 * @param growth            the amount of growth per growth cycle, will only be non-null when type equals
 *                          {@code COMBUSTION} or {@code SMOKE_SCREEN}
 * @param growthInterval    the interval in ticks between growth cycles, will only be non-null when type equals
 *                          {@code COMBUSTION} or {@code SMOKE_SCREEN}
 * @param minDuration       the minimum duration of the effect in ticks, will only be non-null when type equals
 *                          {@code COMBUSTION}, {@code GUN_FIRE_SIMULATION} or {@code SMOKE_SCREEN}
 * @param maxDuration       the maximum duration of the effect in ticks, will only be non-null when type equals
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
 * @param potionEffect      the specification of the potion effect, will only be non-null when type equals
 *                          {@code FLASH}
 * @param activationPattern the specification of the activation pattern, will only be non-null when type equals
 *                          {@code GUN_FIRE_SIMULATION}
 */
public record ItemEffectSpec(
        @NotNull String type,
        @NotNull List<TriggerSpec> triggers,
        @Nullable RangeProfileSpec rangeProfile,
        @Nullable Double minSize,
        @Nullable Double maxSize,
        @Nullable Double density,
        @Nullable Double growth,
        @Nullable Long growthInterval,
        @Nullable Long minDuration,
        @Nullable Long maxDuration,
        @Nullable String activationSounds,
        @Nullable Float power,
        @Nullable Boolean damageBlocks,
        @Nullable Boolean spreadFire,
        @Nullable ParticleEffectSpec particleEffect,
        @Nullable PotionEffectSpec potionEffect,
        @Nullable ActivationPatternSpec activationPattern
) { }
