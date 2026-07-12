package nl.matsgemmeke.battlegrounds.arena.loading;

public class InvalidArenaSetupException extends RuntimeException {

    public InvalidArenaSetupException(String message) {
        super(message);
    }

    public InvalidArenaSetupException(String message, Throwable cause) {
        super(message, cause);
    }
}
