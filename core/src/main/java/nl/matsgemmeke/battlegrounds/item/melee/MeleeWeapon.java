package nl.matsgemmeke.battlegrounds.item.melee;

import nl.matsgemmeke.battlegrounds.item.Interactable;
import nl.matsgemmeke.battlegrounds.item.Weapon;

import java.util.Optional;

public interface MeleeWeapon extends Weapon, Interactable<MeleeWeaponHolder> {

    double getAttackDamage();

    /**
     * Gets the holder of the melee weapon. Returns an empty optional if the melee weapon does not have a holder.
     *
     * @return the optional with the melee weapon holder or empty when there is none
     */
    Optional<MeleeWeaponHolder> getHolder();

    /**
     * Sets the holder of the melee weapon.
     *
     * @param holder the melee weapon holder
     */
    void setHolder(MeleeWeaponHolder holder);
}
