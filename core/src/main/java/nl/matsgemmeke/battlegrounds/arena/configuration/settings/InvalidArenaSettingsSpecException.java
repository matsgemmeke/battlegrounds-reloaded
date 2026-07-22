package nl.matsgemmeke.battlegrounds.arena.configuration.settings;

public class InvalidArenaSettingsSpecException extends RuntimeException {

    public InvalidArenaSettingsSpecException(String message) {
        super(message);
    }

    public InvalidArenaSettingsSpecException(String message, Throwable cause) {
        super(message, cause);
    }
}
