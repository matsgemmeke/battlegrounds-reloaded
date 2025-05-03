package nl.matsgemmeke.battlegrounds.configuration.spec.item.effect;

import org.jetbrains.annotations.NotNull;

/**
 * Represents the immutable, validated configuration for an activation pattern of an item effect.
 *
 * @param burstInterval    the burst interval, defines the activation frequency in the burst
 * @param maxBurstDuration the maximum duration of the burst in ticks
 * @param minBurstDuration the minimum duration of the burst in ticks
 * @param maxDelayDuration the maximum duration of the delay period in ticks
 * @param minDelayDuration the minimum duration of the delay period in ticks
 */
public record ActivationPatternSpec(
        @NotNull Long burstInterval,
        @NotNull Long maxBurstDuration,
        @NotNull Long minBurstDuration,
        @NotNull Long maxDelayDuration,
        @NotNull Long minDelayDuration
) { }
