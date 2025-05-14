package nl.matsgemmeke.battlegrounds.configuration.spec.item.deploy;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the immutable, validated configuration for manual activations loaded from a YAML file.
 *
 * @param delay  the activation delay in ticks
 * @param sounds the raw value for the sounds produced when performing a manual activation
 */
public record ManualActivationSpec(@NotNull Long delay, @Nullable String sounds) { }
