package nl.matsgemmeke.battlegrounds.configuration.validation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public record ValidationContext(
        @NotNull String fieldName,
        @Nullable Object fieldValue,
        @NotNull Map<String, Object> otherFields
) { }
