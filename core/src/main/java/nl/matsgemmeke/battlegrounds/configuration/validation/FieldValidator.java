package nl.matsgemmeke.battlegrounds.configuration.validation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface FieldValidator {

    void validate(@NotNull String name, @Nullable Object value);
}
