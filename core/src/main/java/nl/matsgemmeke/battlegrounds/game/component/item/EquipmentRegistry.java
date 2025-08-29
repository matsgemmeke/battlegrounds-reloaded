package nl.matsgemmeke.battlegrounds.game.component.item;

import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;

public interface EquipmentRegistry {

    /**
     * Gets a list containing all registered equipment items.
     *
     * @return all registered equipment items
     */
    List<Equipment> getAllEquipment();

    /**
     * Gets a list containing all equipment items that are assigned to the given holder.
     *
     * @param holder the equipment holder
     * @return a list of all equipment items assigned to the holder
     */
    List<Equipment> getAssignedEquipment(EquipmentHolder holder);

    /**
     * Gets the equipment that is assigned to a given holder and corresponds with a given item stack. The returned
     * optional will be empty when no matching equipment was found.
     *
     * @param holder the holder to which the equipment should be assigned to
     * @param itemStack the corresponding item stack
     * @return an optional containing the matching equipment or empty when not found
     */
    Optional<Equipment> getAssignedEquipment(EquipmentHolder holder, ItemStack itemStack);

    /**
     * Registers an unassigned equipment item to the registry.
     *
     * @param equipment the equipment item to be registered
     */
    void register(Equipment equipment);

    /**
     * Registers an equipment item to the registry with an assigned holder.
     *
     * @param equipment the equipment item to be registered
     * @param holder the holder associated with the equipment
     */
    void register(Equipment equipment, EquipmentHolder holder);
}
