package nl.matsgemmeke.battlegrounds.item.holder;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.item.Item;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Represents an entity that is capable of holding and operating a {@link Item}.
 */
public interface ItemHolder extends GameEntity {

    /**
     * Sets the {@link ItemStack} that the item holder is holding.
     *
     * @param itemStack the item
     * @return whether the held item was set
     */
    boolean setHeldItem(@Nullable ItemStack itemStack);
}
