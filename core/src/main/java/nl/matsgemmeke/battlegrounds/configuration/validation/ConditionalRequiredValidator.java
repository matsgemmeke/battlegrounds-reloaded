package nl.matsgemmeke.battlegrounds.configuration.validation;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

public class ConditionalRequiredValidator implements FieldValidator<ConditionalRequired> {

    public void validate(@NotNull ValidationContext context, @NotNull ConditionalRequired annotation) {
        Object fieldValue = context.fieldValue();
        String conditionalFieldName = annotation.conditionalFieldName();
        Object conditionalFieldValue = context.otherFields().get(conditionalFieldName);

        if (conditionalFieldValue == null || fieldValue != null) {
            return;
        }

        String[] matchValues = annotation.matchValues();

        if (matchValues.length == 0) {
            throw new ValidationException("Field '%s' is required when '%s' is set".formatted(context.fieldName(), conditionalFieldName));
        }

        if (this.equalsAnyMatchValue(conditionalFieldValue, matchValues)) {
            throw new ValidationException("Field '%s' is required when '%s' equals '%s'".formatted(context.fieldName(), conditionalFieldName, conditionalFieldValue));
        }
    }

    private boolean equalsAnyMatchValue(@NotNull Object value, @NotNull String[] matchValues) {
        return Arrays.stream(matchValues).anyMatch(expectedValue -> Objects.equals(value, expectedValue));
    }
}
