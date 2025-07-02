package nl.matsgemmeke.battlegrounds.configuration.validation;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;

public interface FieldValidator<T extends Annotation> {

    void validate(@NotNull ValidationContext context, @NotNull T annotation);
}
