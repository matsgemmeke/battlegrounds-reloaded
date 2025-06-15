package nl.matsgemmeke.battlegrounds.item.shoot.firemode;

import org.jetbrains.annotations.NotNull;

public class FireModeCreationException extends RuntimeException {

    public FireModeCreationException(@NotNull String message) {
        super(message);
    }
}
