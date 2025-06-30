package nl.matsgemmeke.battlegrounds.configuration.validation;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
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

    public static void validate(@NotNull Object value) {

    }
}
