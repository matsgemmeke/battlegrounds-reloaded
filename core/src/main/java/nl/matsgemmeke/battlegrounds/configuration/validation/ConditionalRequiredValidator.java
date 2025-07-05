package nl.matsgemmeke.battlegrounds.configuration.validation;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ConditionalRequiredValidator implements FieldValidator<ConditionalRequired> {

    public void validate(@NotNull ValidationContext context, @NotNull ConditionalRequired annotation) {
        Object fieldValue = context.fieldValue();
        Object conditionalFieldValue = context.otherFields().get(annotation.conditionalField());
        Object expectedValue = annotation.expectedValue();

        if (conditionalFieldValue != null && fieldValue == null && String.valueOf(expectedValue).isEmpty()) {
            throw new ValidationException("Field '%s' is required when '%s' is set".formatted(context.fieldName(), annotation.conditionalField()));
        }

        if (conditionalFieldValue != null && fieldValue == null && Objects.equals(conditionalFieldValue, expectedValue)) {
            throw new ValidationException("Field '%s' is required when '%s' equals to '%s'".formatted(context.fieldName(), annotation.conditionalField(), expectedValue));
        }
    }
}
