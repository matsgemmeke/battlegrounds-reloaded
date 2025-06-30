package nl.matsgemmeke.battlegrounds.configuration.validation;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ObjectValidator {

    private static Map<Class<? extends Annotation>, FieldValidator> validators = new HashMap<>();

    static {
        registerValidator(Required.class, new RequiredFieldValidator());
    }

    public static void registerValidator(Class<? extends Annotation> annotation, FieldValidator validator) {
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
                    validator.validate(fieldName, fieldValue);
                }
            }
        }
    }
}
