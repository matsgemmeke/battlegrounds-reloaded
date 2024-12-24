package nl.matsgemmeke.battlegrounds.game.component.item;

import nl.matsgemmeke.battlegrounds.game.ItemStorage;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DefaultEquipmentRegistry implements EquipmentRegistry {

    @NotNull
    private ItemStorage<Equipment, EquipmentHolder> equipmentStorage;

    public DefaultEquipmentRegistry(@NotNull ItemStorage<Equipment, EquipmentHolder> equipmentStorage) {
        this.equipmentStorage = equipmentStorage;
    }

    @NotNull
    public List<Equipment> findAll() {
        return equipmentStorage.getAllItems();
    }

    public void registerItem(@NotNull Equipment equipment) {
        equipmentStorage.addUnassignedItem(equipment);
    }

    public void registerItem(@NotNull Equipment equipment, @NotNull EquipmentHolder holder) {
        equipmentStorage.addAssignedItem(equipment, holder);
    }

    public void unassignItem(@NotNull Equipment equipment) {
        EquipmentHolder holder = equipment.getHolder();

        if (holder == null) {
            return;
        }

        equipmentStorage.removeAssignedItem(equipment, holder);
        equipmentStorage.addUnassignedItem(equipment);
    }
}
