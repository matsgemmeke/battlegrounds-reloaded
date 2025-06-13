package nl.matsgemmeke.battlegrounds.game.component.item;

import nl.matsgemmeke.battlegrounds.game.ItemContainer;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DefaultEquipmentRegistry implements EquipmentRegistry {

    @NotNull
    private final ItemContainer<Equipment, EquipmentHolder> equipmentContainer;

    public DefaultEquipmentRegistry(@NotNull ItemContainer<Equipment, EquipmentHolder> equipmentContainer) {
        this.equipmentContainer = equipmentContainer;
    }

    @NotNull
    public List<Equipment> findAll() {
        return equipmentContainer.getAllItems();
    }

    @NotNull
    public List<Equipment> getAssignedItems(@NotNull EquipmentHolder holder) {
        return equipmentContainer.getAssignedItems(holder);
    }

    public void registerItem(@NotNull Equipment equipment) {
        equipmentContainer.addUnassignedItem(equipment);
    }

    public void registerItem(@NotNull Equipment equipment, @NotNull EquipmentHolder holder) {
        equipmentContainer.addAssignedItem(equipment, holder);
    }

    public void unassignItem(@NotNull Equipment equipment) {
        EquipmentHolder holder = equipment.getHolder();

        if (holder == null) {
            return;
        }

        equipmentContainer.removeAssignedItem(equipment, holder);
        equipmentContainer.addUnassignedItem(equipment);
    }
}
