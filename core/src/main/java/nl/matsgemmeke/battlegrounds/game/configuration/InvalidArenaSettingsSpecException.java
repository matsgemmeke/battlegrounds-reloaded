package nl.matsgemmeke.battlegrounds.game.configuration;

public class InvalidArenaSettingsSpecException extends RuntimeException {

    public InvalidArenaSettingsSpecException(String message) {
        super(message);
    }

    public InvalidArenaSettingsSpecException(String message, Throwable cause) {
        super(message, cause);
    }
}
