package nl.matsgemmeke.battlegrounds.item;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Represents an entity that is capable of holding and operating a {@link Item}.
 */
public interface ItemHolder extends GameEntity {

    /**
     * Gets whether the entity is currently able to throw items.
     *
     * @return whether the entity can throw items
     */
    boolean isAbleToThrow();

    /**
     * Sets whether the entity is currently able to throw items.
     *
     * @param ableToThrow whether the entity can throw items
     */
    void setAbleToThrow(boolean ableToThrow);

    /**
     * Sets the {@link ItemStack} that the item holder is holding.
     *
     * @param itemStack the item
     * @return whether the held item was set
     */
    boolean setHeldItem(@Nullable ItemStack itemStack);
}
