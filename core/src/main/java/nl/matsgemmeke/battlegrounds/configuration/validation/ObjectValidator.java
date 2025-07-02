package nl.matsgemmeke.battlegrounds.configuration.validation;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

public class ObjectValidator {

    private static Map<Class<? extends Annotation>, FieldValidator<?>> validators = new HashMap<>();

    static {
        registerValidator(Required.class, new RequiredFieldValidator());
        registerValidator(EnumValue.class, new EnumValueValidator());
    }

    public static void registerValidator(Class<? extends Annotation> annotation, FieldValidator<?> validator) {
        validators.put(annotation, validator);
    }

    public static void validate(@NotNull Object object) {
        for (Field field : object.getClass().getDeclaredFields()) {
            String fieldName = field.getName();
            Object fieldValue;

            try {
                fieldValue = field.get(object);
            } catch (IllegalAccessException e) {
                throw new ValidationException("Cannot validate field '%s' because it is not marked public".formatted(fieldName));
            }

            for (Annotation annotation : field.getAnnotations()) {
                FieldValidator validator = validators.get(annotation.annotationType());

                if (validator != null) {
                    Map<String, Object> otherFields = extractOtherFields(object, field);

                    ValidationContext context = new ValidationContext(fieldName, fieldValue, otherFields);
                    validator.validate(context, annotation);
                }
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
}
