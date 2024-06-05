package nl.matsgemmeke.battlegrounds.item;

import org.bukkit.inventory.ItemStack;

public interface Weapon extends Item {

    /**
     * Updates the {@link ItemStack} of the weapon.
     *
     * @return whether the weapon was updated
     */
    boolean update();
}
