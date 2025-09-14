package nl.matsgemmeke.battlegrounds.game.component.item;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DefaultItemLifecycleHandler implements ItemLifecycleHandler {

    @NotNull
    private final EquipmentRegistry equipmentRegistry;

    @Inject
    public DefaultItemLifecycleHandler(@NotNull EquipmentRegistry equipmentRegistry) {
        this.equipmentRegistry = equipmentRegistry;
    }

    public void cleanupItems(@NotNull GamePlayer gamePlayer) {
        List<Equipment> equipmentList = equipmentRegistry.getAssignedEquipment(gamePlayer);

        equipmentList.forEach(Equipment::cleanup);
    }
}
