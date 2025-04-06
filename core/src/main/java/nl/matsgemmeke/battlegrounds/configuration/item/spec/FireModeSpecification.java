package nl.matsgemmeke.battlegrounds.configuration.item.spec;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public record FireModeSpecification(@NotNull String type, @NotNull Map<String, Object> properties) { }
