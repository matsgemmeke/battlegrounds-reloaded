package nl.matsgemmeke.battlegrounds.game.component;

import nl.matsgemmeke.battlegrounds.item.ItemStorage;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import org.jetbrains.annotations.NotNull;

public class DefaultEquipmentRegistry implements ItemRegistry<Equipment, EquipmentHolder> {

    @NotNull
    private ItemStorage<Equipment, EquipmentHolder> equipmentStorage;

    public DefaultEquipmentRegistry(@NotNull ItemStorage<Equipment, EquipmentHolder> equipmentStorage) {
        this.equipmentStorage = equipmentStorage;
    }

    public void registerItem(@NotNull Equipment item) {
        equipmentStorage.addUnassignedItem(item);
    }

    public void registerItem(@NotNull Equipment item, @NotNull EquipmentHolder holder) {
        equipmentStorage.addAssignedItem(item, holder);
    }
}
