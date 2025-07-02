package nl.matsgemmeke.battlegrounds.configuration.validation;

import org.jetbrains.annotations.NotNull;

public class RegexFieldValidator implements FieldValidator<Regex> {

    public void validate(@NotNull ValidationContext context, @NotNull Regex annotation) {
        String fieldValue = String.valueOf(context.fieldValue());

        if (fieldValue.equals("null") || fieldValue.matches(annotation.pattern())) {
            return;
        }

        throw new ValidationException("The value '%s' for field '%s' does not match the required pattern".formatted(fieldValue, context.fieldName()));
    }
}
