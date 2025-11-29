package nl.matsgemmeke.battlegrounds.game.component.item;

import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeapon;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeaponHolder;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public interface MeleeWeaponRegistry {

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
