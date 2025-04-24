package nl.matsgemmeke.battlegrounds.configuration.spec.equipment;

import org.jetbrains.annotations.Nullable;

public record ControlsSpec(
        @Nullable String throwAction,
        @Nullable String cookAction,
        @Nullable String placeAction,
        @Nullable String activateAction
) { }
