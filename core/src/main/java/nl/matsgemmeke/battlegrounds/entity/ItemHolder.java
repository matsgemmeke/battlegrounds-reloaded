package nl.matsgemmeke.battlegrounds.entity;

import nl.matsgemmeke.battlegrounds.item.Item;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an entity that is capable of holding and operating a {@link Item}.
 */
public interface ItemHolder extends GameEntity {

    /**
     * Updates the {@link ItemStack} of a {@link Item} in the holder's inventory.
     *
     * @param itemStack the item
     * @return whether the item was updated
     */
    boolean updateItemStack(@NotNull ItemStack itemStack);
}
