package nl.matsgemmeke.battlegrounds.game.component.item;

import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentUser;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;

public interface EquipmentRegistry {

    /**
     * Assigns the given equipment to the given user. This method will do nothing when the given equipment is not
     * registered to the registry, or when it is already assigned.
     *
     * @param equipment the equipment
     * @param user      the user to assign the equipment to
     */
    void assign(Equipment equipment, EquipmentUser user);

    /**
     * Removes the assigned user from the given equipment and registers it as unassigned. This method will do nothing
     * when the given equipment is not registered to the registry or has no assigned user.
     *
     * @param equipment the equipment to be unassigned from its user
     */
    void unassign(Equipment equipment);

    /**
     * Gets a list containing all registered equipment items.
     *
     * @return all registered equipment items
     */
    List<Equipment> getAllEquipment();

    /**
     * Gets a list containing all equipment items that are assigned to the given user.
     *
     * @param user the equipment user
     * @return     a list of all equipment items assigned to the user
     */
    List<Equipment> getAssignedEquipmentList(EquipmentUser user);

    /**
     * Gets the equipment that is assigned to a given user and corresponds with a given item stack. The returned
     * optional will be empty when no matching equipment was found.
     *
     * @param user      the user to which the equipment should be assigned to
     * @param itemStack the corresponding item stack
     * @return          an optional containing the matching equipment or empty when not found
     */
    Optional<Equipment> getAssignedEquipment(EquipmentUser user, ItemStack itemStack);

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
     * Registers an equipment item to the registry with an assigned user.
     *
     * @param equipment the equipment item to be registered
     * @param user      the user associated with the equipment
     */
    void register(Equipment equipment, EquipmentUser user);
}
