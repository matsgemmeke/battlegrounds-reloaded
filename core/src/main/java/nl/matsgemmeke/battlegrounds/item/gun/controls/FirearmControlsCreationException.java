package nl.matsgemmeke.battlegrounds.item.gun.controls;

import org.jetbrains.annotations.NotNull;

public class FirearmControlsCreationException extends RuntimeException {

    public FirearmControlsCreationException(@NotNull String message) {
        super(message);
    }
}
