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

    public void registerItem(@NotNull Equipment equipment) {
        equipmentStorage.addUnassignedItem(equipment);
    }

    public void registerItem(@NotNull Equipment equipment, @NotNull EquipmentHolder holder) {
        equipmentStorage.addAssignedItem(equipment, holder);
    }
}
