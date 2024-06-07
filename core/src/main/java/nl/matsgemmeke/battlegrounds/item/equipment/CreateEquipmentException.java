package nl.matsgemmeke.battlegrounds.item.equipment;

import org.jetbrains.annotations.NotNull;

public class CreateEquipmentException extends RuntimeException {

    public CreateEquipmentException(@NotNull String message) {
        super(message);
    }
}
