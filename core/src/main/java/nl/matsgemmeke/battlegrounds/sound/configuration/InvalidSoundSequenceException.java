package nl.matsgemmeke.battlegrounds.sound.configuration;

import java.util.List;

public class InvalidSoundSequenceException extends Exception {

    private final List<String> errors;

    public InvalidSoundSequenceException(String message, List<String> errors) {
        super(message);
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}
