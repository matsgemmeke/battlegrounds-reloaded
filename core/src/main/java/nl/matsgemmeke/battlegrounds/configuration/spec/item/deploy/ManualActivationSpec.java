package nl.matsgemmeke.battlegrounds.configuration.spec.item.deploy;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the immutable, validated configuration for manual activations loaded from a YAML file.
 *
 * @param activationDelay the activation delay in ticks
 * @param activationSounds the raw value for the sounds produced when performing a manual activation
 */
public record ManualActivationSpec(@NotNull Long activationDelay, @Nullable String activationSounds) { }
