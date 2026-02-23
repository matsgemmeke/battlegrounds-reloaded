package nl.matsgemmeke.battlegrounds.game.component.item;

import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;

public interface EquipmentRegistry {

    /**
     * Assigns the given equipment to the given holder. This method will do nothing when the given equipment is not
     * registered to the registry, or when it is already assigned.
     *
     * @param equipment the equipment
     * @param holder    the holder to assign the equipment to
     */
    void assign(Equipment equipment, EquipmentHolder holder);

    /**
     * Removes the assigned holder from the given equipment and registers it as unassigned. This method will do nothing
     * when the given equipment is not registered to the registry or has no assigned holder.
     *
     * @param equipment the equipment to be unassigned from its holder
     */
    void unassign(Equipment equipment);

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
    List<Equipment> getAssignedEquipmentList(EquipmentHolder holder);

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
     * Gets the unassigned equipment that corresponds with a given item stack. The returned optional will be empty when
     * no matching equipment was found.
     *
     * @param itemStack the corresponding item stack
     * @return          an optional containing the matching equipment or empty when not found
     */
    Optional<Equipment> getUnassignedEquipment(ItemStack itemStack);

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
