package nl.matsgemmeke.battlegrounds.command;

public class UnknownGameKeyException extends RuntimeException {

    public UnknownGameKeyException(String message) {
        super(message);
    }
}
