package nl.matsgemmeke.battlegrounds.configuration.spec;

import nl.matsgemmeke.battlegrounds.configuration.item.InvalidItemConfigurationException;
import nl.matsgemmeke.battlegrounds.configuration.validation.ValidationResult;
import nl.matsgemmeke.battlegrounds.configuration.validation.Validator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FieldSpecResolver<T> {

    @NotNull
    private final List<Validator<T>> validators;
    @Nullable
    private String route;
    @Nullable
    private T value;

    public FieldSpecResolver() {
        this.validators = new ArrayList<>();
    }

    @NotNull
    public FieldSpecResolver<T> route(@NotNull String route) {
        this.route = route;
        return this;
    }

    @NotNull
    public FieldSpecResolver<T> value(@Nullable T value) {
        this.value = value;
        return this;
    }

    @NotNull
    public FieldSpecResolver<T> validate(@NotNull Validator<T> validator) {
        validators.add(validator);
        return this;
    }

    public T resolve() {
        if (route == null) {
            throw new InvalidItemConfigurationException("Cannot resolve value '%s' without assigned configuration route".formatted(value));
        }

        FieldSpec<T> spec = new FieldSpec<>(route);
        validators.forEach(spec::withValidator);

        ValidationResult<T> result = spec.getValidatedValue(value);

        if (!result.isValid()) {
            throw new InvalidItemConfigurationException(result.getErrorMessage());
        }

        return result.getValue();
    }
}
