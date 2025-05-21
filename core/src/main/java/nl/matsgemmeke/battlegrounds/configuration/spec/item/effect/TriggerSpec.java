package nl.matsgemmeke.battlegrounds.configuration.spec.item.effect;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Represents the immutable, validated configuration for an item effect trigger loaded from a YAML file.
 *
 * @param type         the trigger type
 * @param delay        the delay in ticks used for checks, will only be non-null when type equals to
 *                     {@code ENEMY_PROXIMITY}, {@code FLOOR_HIT} or {@code IMPACT}
 * @param interval     the interval used during checks, will only be non-null when type equals to
 *                     {@code ENEMY_PROXIMITY}, {@code FLOOR_HIT} or {@code IMPACT}
 * @param offsetDelays the offset delays used for scheduling trigger activations, will only be non-null when type
 *                     equals to {@code SEQUENCE}
 * @param range        the size of the range used for trigger checks, will only be non-null when type equals to
 *                     {@code ENEMY_PROXIMITY}
 */
public record TriggerSpec(
        @NotNull String type,
        @Nullable Long delay,
        @Nullable Long interval,
        @Nullable List<Long> offsetDelays,
        @Nullable Double range
) { }
