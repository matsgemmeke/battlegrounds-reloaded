package nl.matsgemmeke.battlegrounds.storage.state.equipment;

import java.util.UUID;

public record EquipmentState(UUID playerUuid, String equipmentName, int itemSlot) {
}
