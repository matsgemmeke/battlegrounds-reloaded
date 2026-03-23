package nl.matsgemmeke.battlegrounds.item.melee;

import nl.matsgemmeke.battlegrounds.item.Interactable;
import nl.matsgemmeke.battlegrounds.item.Weapon;
import nl.matsgemmeke.battlegrounds.item.reload.Reloadable;
import nl.matsgemmeke.battlegrounds.item.throwing.ThrowableItem;

import java.util.Optional;

public interface MeleeWeapon extends Weapon, Reloadable, ThrowableItem, Interactable<MeleeWeaponUser> {

    double getAttackDamage();

    /**
     * Assigns a user to the melee weapon.
     *
     * @param user the user to assign
     */
    void assign(MeleeWeaponUser user);

    /**
     * Unassigns the current user from the melee weapon. This method will have no effect when the melee weapon has no
     * user assigned.
     */
    void unassign();

    /**
     * Gets the user of the melee weapon. Returns an empty optional if the melee weapon does not have a user.
     *
     * @return the optional with the melee weapon user or empty when there is none
     */
    Optional<MeleeWeaponUser> getUser();
}
