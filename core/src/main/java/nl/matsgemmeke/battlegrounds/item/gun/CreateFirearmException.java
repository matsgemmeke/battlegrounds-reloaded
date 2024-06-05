package nl.matsgemmeke.battlegrounds.item.gun;

import org.jetbrains.annotations.NotNull;

public class CreateFirearmException extends RuntimeException {

    public CreateFirearmException(@NotNull String message) {
        super(message);
    }
}
