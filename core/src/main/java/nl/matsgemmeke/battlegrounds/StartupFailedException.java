package nl.matsgemmeke.battlegrounds;

public class StartupFailedException extends RuntimeException {

    public StartupFailedException(String message) {
        super(message);
    }

    public StartupFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
