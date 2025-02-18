package nl.matsgemmeke.battlegrounds.item.equipment;

import org.jetbrains.annotations.NotNull;

public class EquipmentCreationException extends RuntimeException {

    public EquipmentCreationException(@NotNull String message) {
        super(message);
    }
}
