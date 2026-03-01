package nl.matsgemmeke.battlegrounds.item.melee;

import nl.matsgemmeke.battlegrounds.item.Interactable;
import nl.matsgemmeke.battlegrounds.item.Weapon;
import nl.matsgemmeke.battlegrounds.item.reload.Reloadable;
import nl.matsgemmeke.battlegrounds.item.throwing.ThrowableItem;

import java.util.Optional;

public interface MeleeWeapon extends Weapon, Reloadable, ThrowableItem, Interactable<MeleeWeaponHolder> {

    double getAttackDamage();

    /**
     * Assigns a holder to the melee weapon.
     *
     * @param holder the holder to assign
     */
    void assign(MeleeWeaponHolder holder);

    /**
     * Unassigns the current holder from the melee weapon. This method will have no effect when the melee weapon has no
     * holder assigned.
     */
    void unassign();

    /**
     * Gets the holder of the melee weapon. Returns an empty optional if the melee weapon does not have a holder.
     *
     * @return the optional with the melee weapon holder or empty when there is none
     */
    Optional<MeleeWeaponHolder> getHolder();
}
