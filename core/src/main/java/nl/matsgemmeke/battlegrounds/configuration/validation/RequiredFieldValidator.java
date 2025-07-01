package nl.matsgemmeke.battlegrounds.configuration.validation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RequiredFieldValidator implements FieldValidator<Required> {

    public void validate(@NotNull String name, @Nullable Object value, @NotNull Required annotation) {
        if (value != null) {
            return;
        }

        throw new ValidationException("Field '%s' is required but no value is provided".formatted(name));
    }
}
