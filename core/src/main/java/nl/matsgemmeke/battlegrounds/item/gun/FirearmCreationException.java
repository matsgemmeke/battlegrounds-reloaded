package nl.matsgemmeke.battlegrounds.item.gun;

import org.jetbrains.annotations.NotNull;

public class FirearmCreationException extends RuntimeException {

    public FirearmCreationException(@NotNull String message) {
        super(message);
    }
}
