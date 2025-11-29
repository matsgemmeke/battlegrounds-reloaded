package nl.matsgemmeke.battlegrounds.item.melee;

import nl.matsgemmeke.battlegrounds.item.ItemHolder;

public interface MeleeWeaponHolder extends ItemHolder {

    /**
     * Returns a float value between 0.0 and 1.0, represents the amount of relative damage the holder does when
     * performing an attack.
     *
     * @return the relative attack strength
     */
    float getAttackStrength();
}
