package nl.matsgemmeke.battlegrounds.game.component.item;

import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeapon;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeaponHolder;

public interface MeleeWeaponRegistry {

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
