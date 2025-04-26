package nl.matsgemmeke.battlegrounds.configuration.spec.equipment;

import org.jetbrains.annotations.Nullable;

/**
 * Represents the immutable, validated configuration for an equipment item's controls loaded from a YAML file.
 *
 * @param throwAction the action value used to perform the throw function
 * @param cookAction the action value used to perform the cook function
 * @param placeAction the action value used to perform the place function
 * @param activateAction the action value used to perform the activate function
 */
public record ControlsSpec(
        @Nullable String throwAction,
        @Nullable String cookAction,
        @Nullable String placeAction,
        @Nullable String activateAction
) { }
