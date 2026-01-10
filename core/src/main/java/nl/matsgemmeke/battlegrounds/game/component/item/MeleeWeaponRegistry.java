package nl.matsgemmeke.battlegrounds.game.component.item;

import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeapon;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeaponHolder;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;

public interface MeleeWeaponRegistry {

    /**
     * Assigns the given melee weapon to the given holder. This method will do nothing when the given melee weapon is
     * not registered to the registry, or when it is already assigned.
     *
     * @param meleeWeapon the melee weapon
     * @param holder      the holder to assign the melee weapon to
     */
    void assign(MeleeWeapon meleeWeapon, MeleeWeaponHolder holder);

    /**
     * Removes the assigned holder from the given melee weapon and registers it as unassigned. This method will do
     * nothing when the given melee weapon is not registered to the registry or has no assigned holder.
     *
     * @param meleeWeapon the melee weapon to be unassigned from its holder
     */
    void unassign(MeleeWeapon meleeWeapon);

    /**
     * Gets the melee weapon that is assigned to a given holder and corresponds with a given item stack. The returned
     * optional will be empty when no matching melee weapon was found.
     *
     * @param holder    the holder to which the melee weapon should be assigned to
     * @param itemStack the corresponding item stack
     * @return          an optional containing the matching melee weapon or empty when not found
     */
    Optional<MeleeWeapon> getAssignedMeleeWeapon(MeleeWeaponHolder holder, ItemStack itemStack);

    /**
     * Gets all registered melee weapons that are assigned to a given holder.
     *
     * @param holder the item holder
     * @return       a list of melee weapons assigned to the holder
     */
    List<MeleeWeapon> getAssignedMeleeWeapons(MeleeWeaponHolder holder);

    /**
     * Registers an unassigned melee weapon to the registry.
     *
     * @param meleeWeapon the melee weapon to be registered
     */
    void register(MeleeWeapon meleeWeapon);

    /**
     * Registers a melee weapon to the registry with an assigned holder.
     *
     * @param meleeWeapon the melee weapon to be registered
     * @param holder      the holder associated with the melee weapon
     */
    void register(MeleeWeapon meleeWeapon, MeleeWeaponHolder holder);
}
