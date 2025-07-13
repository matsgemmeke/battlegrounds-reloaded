package nl.matsgemmeke.battlegrounds.configuration.validation;

import org.jetbrains.annotations.NotNull;

public class RequiredValidator implements Validator<Required> {

    public void validate(@NotNull ValidationContext context, @NotNull Required annotation) {
        if (context.fieldValue() != null) {
            return;
        }

        throw new ValidationException("Field '%s' is required but no value is provided".formatted(context.fieldName()));
    }
}
