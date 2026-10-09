package nl.matsgemmeke.battlegrounds.sound.configuration;

import java.util.List;

public class InvalidSoundTemplateException extends Exception {

    private final List<String> errors;

    public InvalidSoundTemplateException(String message, List<String> errors) {
        super(message);
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}
