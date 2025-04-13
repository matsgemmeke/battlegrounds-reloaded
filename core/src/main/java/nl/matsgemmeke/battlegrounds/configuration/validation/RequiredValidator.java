package nl.matsgemmeke.battlegrounds.configuration.validation;

import java.util.Optional;

public class RequiredValidator<T> implements Validator<T> {

    public Optional<String> validate(String route, T value) {
        if (value == null) {
            return Optional.of("Missing required '%s' value".formatted(route));
        }

        return Optional.empty();
    }
}
