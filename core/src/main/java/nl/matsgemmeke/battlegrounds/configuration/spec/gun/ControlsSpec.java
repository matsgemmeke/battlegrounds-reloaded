package nl.matsgemmeke.battlegrounds.configuration.spec.gun;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the immutable, validated configuration for a gun item's controls loaded from a YAML file.
 *
 * @param reloadAction the action value used to perform the reload function
 * @param shootAction the action value used to perform the shoot function
 * @param useScopeAction the action value used to perform the use scope function
 * @param stopScopeAction the action value used to perform the stop scope function
 * @param changeScopeMagnificationAction the action value used to perform the change scope magnification function
 */
public record ControlsSpec(
        @NotNull String reloadAction,
        @NotNull String shootAction,
        @Nullable String useScopeAction,
        @Nullable String stopScopeAction,
        @Nullable String changeScopeMagnificationAction
) { }
