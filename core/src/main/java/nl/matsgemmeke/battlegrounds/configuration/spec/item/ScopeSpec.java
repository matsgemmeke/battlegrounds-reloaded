package nl.matsgemmeke.battlegrounds.configuration.spec.item;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Represents the immutable, validated configuration for a spread pattern loaded from a YAML file.
 *
 * @param magnifications the magnifications settings
 * @param useSounds the optional raw value that defines the sounds the scope makes when being used
 * @param stopSounds the optional raw value that defines the sounds the gun makes when stopped being used
 * @param changeMagnificationSounds the optional raw value that defines the sounds the gun makes when changing its magnification
 */
public record ScopeSpec(
        @NotNull List<Float> magnifications,
        @Nullable String useSounds,
        @Nullable String stopSounds,
        @Nullable String changeMagnificationSounds
) { }
