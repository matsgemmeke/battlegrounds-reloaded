package com.github.matsgemmeke.battlegrounds.api.item;

import org.bukkit.inventory.ItemStack;

public interface Weapon extends BattleItem {

    /**
     * Updates the {@link ItemStack} of the weapon.
     *
     * @return whether the weapon was updated
     */
    boolean update();
}
