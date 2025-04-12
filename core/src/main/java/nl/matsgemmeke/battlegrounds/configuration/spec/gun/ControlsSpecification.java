package nl.matsgemmeke.battlegrounds.configuration.spec.gun;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ControlsSpecification(
        @NotNull String reloadAction,
        @NotNull String shootAction,
        @Nullable String useScopeAction,
        @Nullable String stopScopeAction,
        @Nullable String changeScopeMagnificationAction
) { }
