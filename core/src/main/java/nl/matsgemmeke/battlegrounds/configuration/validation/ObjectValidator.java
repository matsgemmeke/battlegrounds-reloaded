package nl.matsgemmeke.battlegrounds.configuration.validation;

import nl.matsgemmeke.battlegrounds.configuration.validation.constraint.EnumValues;
import nl.matsgemmeke.battlegrounds.configuration.validation.constraint.EnumValuesValidator;
import nl.matsgemmeke.battlegrounds.configuration.validation.constraint.Size;
import nl.matsgemmeke.battlegrounds.configuration.validation.constraint.SizeConstraintValidator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

public class ObjectValidator {

    private static final Map<Class<? extends Annotation>, Validator<?>> validators = new HashMap<>();

    static {
        registerValidator(Required.class, new RequiredValidator());
        registerValidator(EnumValue.class, new EnumValueValidator());
        registerValidator(EnumValues.class, new EnumValuesValidator());
        registerValidator(Regex.class, new RegexValidator());
        registerValidator(Size.class, new SizeConstraintValidator());
        registerValidator(ConditionalRequired.class, new ConditionalRequiredValidator());
    }

    public static void registerValidator(Class<? extends Annotation> annotation, Validator<?> validator) {
        validators.put(annotation, validator);
    }

    public static void validate(Object object) {
        for (Field field : object.getClass().getDeclaredFields()) {
            String fieldName = field.getName();
            Object fieldValue;

            try {
                fieldValue = field.get(object);
            } catch (IllegalAccessException e) {
                throw new ValidationException("Cannot validate field '%s': %s".formatted(fieldName, e.getMessage()));
            }

            for (Annotation annotation : field.getAnnotations()) {
                Validator validator = validators.get(annotation.annotationType());

                if (validator != null) {
                    Map<String, Object> otherFields = extractOtherFields(object, field);

                    ValidationContext context = new ValidationContext(fieldName, fieldValue, otherFields);
                    validator.validate(context, annotation);
                }
            }

            if (fieldValue != null && containsNestedVariables(fieldValue)) {
                validate(fieldValue);
            }
        }
    }

    private static Map<String, Object> extractOtherFields(Object object, Field excludeField) {
        return Arrays.stream(object.getClass().getDeclaredFields())
                .filter(field -> !field.equals(excludeField))
                .peek(field -> field.setAccessible(true))
                .map(field -> Map.entry(field.getName(), extractFieldValue(object, field)))
                .filter(entry -> entry.getValue().isPresent())
                .collect(Collectors.toMap(Entry::getKey, entry -> entry.getValue().get()));
    }

    private static Optional<Object> extractFieldValue(Object object, Field field) {
        try {
            return Optional.ofNullable(field.get(object));
        } catch (IllegalAccessException e) {
            throw new ValidationException("Unable to extract field '%s': %s".formatted(field.getName(), e.getMessage()));
        }
    }

    private static boolean containsNestedVariables(Object fieldValue) {
        return fieldValue.getClass().getName().endsWith("Spec");
    }
}
