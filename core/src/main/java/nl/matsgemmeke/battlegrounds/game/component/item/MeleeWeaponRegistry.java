package nl.matsgemmeke.battlegrounds.game.component.item;

import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeapon;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeaponUser;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;

public interface MeleeWeaponRegistry {

    /**
     * Gets the melee weapon that is assigned to a given holder and corresponds with a given item stack. The returned
     * optional will be empty when no matching melee weapon was found.
     *
     * @param user      the user to which the melee weapon should be assigned to
     * @param itemStack the corresponding item stack
     * @return          an optional containing the matching melee weapon or empty when not found
     */
    Optional<MeleeWeapon> getAssignedMeleeWeapon(MeleeWeaponUser user, ItemStack itemStack);

    /**
     * Gets all registered melee weapons that are assigned to a given user.
     *
     * @param user   the melee weapon user
     * @return       a list of melee weapons assigned to the given user
     */
    List<MeleeWeapon> getAssignedMeleeWeapons(MeleeWeaponUser user);

    /**
     * Gets the unassigned melee weapon that corresponds with a given item stack. The returned optional will be empty
     * when no matching melee weapon was found.
     *
     * @param itemStack the corresponding item stack
     * @return          an optional containing the matching melee weapon or empty when not found
     */
    Optional<MeleeWeapon> getUnassignedMeleeWeapon(ItemStack itemStack);

    /**
     * Registers a melee weapon to the registry.
     *
     * @param meleeWeapon the melee weapon to be registered
     */
    void register(MeleeWeapon meleeWeapon);
}
