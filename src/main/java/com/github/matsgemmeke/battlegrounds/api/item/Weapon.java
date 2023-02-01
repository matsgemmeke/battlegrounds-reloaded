package com.github.matsgemmeke.battlegrounds.api.item;

public interface Weapon extends BattleItem {

    /**
     * Updates the ItemStack of the weapon.
     *
     * @return whether the ItemStack was updated
     */
    boolean update();
}
