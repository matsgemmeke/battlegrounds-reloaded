package nl.matsgemmeke.battlegrounds.item;

import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An item that can be dropped.
 */
public interface Droppable {

    /**
     * Gets the dropped item entity. Returns null if the item is not yet dropped.
     *
     * @return the dropped item entity
     */
    @Nullable
    Item getDroppedItem();

    /**
     * Gets the item stack of the item. Returns null if the item has no set item stack.
     *
     * @return the item's item stack
     */
    @Nullable
    ItemStack getItemStack();

    /**
     * Gets whether the item is able to drop an item.
     *
     * @return whether the item can be dropped
     */
    boolean canDrop();

    /**
     * Drops an {@link Item} entity at a given location.
     *
     * @param location the drop location
     * @return a new dropped item entity
     */
    @NotNull
    Item dropItem(@NotNull Location location);
}
