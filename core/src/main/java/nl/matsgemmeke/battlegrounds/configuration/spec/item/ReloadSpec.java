package nl.matsgemmeke.battlegrounds.configuration.spec.item;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the immutable, validated configuration for a reload system loaded from a YAML file.
 *
 * @param type the reload system type
 * @param reloadSounds the optional raw value that defines the sounds the system making when reloading
 * @param duration the duration of a reload cycle in ticks
 */
public record ReloadSpec(@NotNull String type, @Nullable String reloadSounds, @NotNull Long duration) { }
