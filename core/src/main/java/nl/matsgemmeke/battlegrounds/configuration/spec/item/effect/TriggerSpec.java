package nl.matsgemmeke.battlegrounds.configuration.spec.item.effect;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the immutable, validated configuration for an item effect trigger loaded from a YAML file.
 *
 * @param type                 the trigger type
 * @param checkRange           the size of the range used for trigger checks, will only be non-null when type equals to
 *                             {@code ENEMY_PROXIMITY}
 * @param checkInterval        the interval in ticks between trigger checks, will only be non-null when type
 *                             equals to {@code ENEMY_PROXIMITY} or {@code FLOOR_HIT}
 * @param delayUntilActivation the delay in ticks until the trigger is automatically activated, will only be non-null
 *                             when type equals to {@code TIMED}
 */
public record TriggerSpec(
        @NotNull String type,
        @Nullable Double checkRange,
        @Nullable Long checkInterval,
        @Nullable Long delayUntilActivation
) { }
