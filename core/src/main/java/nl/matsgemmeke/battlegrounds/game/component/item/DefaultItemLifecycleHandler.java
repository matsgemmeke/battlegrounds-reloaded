package nl.matsgemmeke.battlegrounds.game.component.item;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DefaultItemLifecycleHandler implements ItemLifecycleHandler {

    @NotNull
    private final EquipmentRegistry equipmentRegistry;

    public DefaultItemLifecycleHandler(@NotNull EquipmentRegistry equipmentRegistry) {
        this.equipmentRegistry = equipmentRegistry;
    }

    public void cleanupItems(@NotNull GamePlayer gamePlayer) {
        List<Equipment> equipmentList = equipmentRegistry.getAssignedItems(gamePlayer);

        equipmentList.forEach(Equipment::cleanup);
    }
}
