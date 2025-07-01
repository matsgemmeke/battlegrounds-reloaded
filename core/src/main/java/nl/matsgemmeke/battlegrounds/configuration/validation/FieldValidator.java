package nl.matsgemmeke.battlegrounds.configuration.validation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;

public interface FieldValidator<T extends Annotation> {

    void validate(@NotNull String name, @Nullable Object value, @NotNull T annotation);
}
