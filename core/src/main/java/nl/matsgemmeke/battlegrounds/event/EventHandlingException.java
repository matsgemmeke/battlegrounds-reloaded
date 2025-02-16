package nl.matsgemmeke.battlegrounds.event;

public class EventHandlingException extends RuntimeException {

    public EventHandlingException(String message) {
        super(message);
    }

    public EventHandlingException(String message, Throwable cause) {
        super(message, cause);
    }
}
