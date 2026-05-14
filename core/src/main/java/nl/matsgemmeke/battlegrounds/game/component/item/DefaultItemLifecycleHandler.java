package nl.matsgemmeke.battlegrounds.game.component.item;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;

import java.util.List;

public class DefaultItemLifecycleHandler implements ItemLifecycleHandler {

    private final EquipmentRegistry equipmentRegistry;

    @Inject
    public DefaultItemLifecycleHandler(EquipmentRegistry equipmentRegistry) {
        this.equipmentRegistry = equipmentRegistry;
    }

    public void resetItems(GamePlayer gamePlayer) {
        List<Equipment> equipmentList = equipmentRegistry.getAssignedEquipmentList(gamePlayer);

        equipmentList.forEach(Equipment::reset);
    }
}
