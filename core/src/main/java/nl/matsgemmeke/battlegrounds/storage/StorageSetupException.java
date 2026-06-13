package nl.matsgemmeke.battlegrounds.storage;

public class StorageSetupException extends RuntimeException {

    public StorageSetupException(String message) {
        super(message);
    }

    public StorageSetupException(String message, Throwable cause) {
        super(message, cause);
    }
}
