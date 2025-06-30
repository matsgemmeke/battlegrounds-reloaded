package nl.matsgemmeke.battlegrounds.configuration.validation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RequiredFieldValidator implements FieldValidator {

    public void validate(@NotNull String name, @Nullable Object value) {
        if (value != null) {
            return;
        }

        throw new ValidationException("Field '%s' is required but no value is provided".formatted(name));
    }
}
