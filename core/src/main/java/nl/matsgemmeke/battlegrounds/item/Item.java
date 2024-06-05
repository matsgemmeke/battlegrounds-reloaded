package nl.matsgemmeke.battlegrounds.item;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An item object that appears in game instances.
 */
public interface Item {

    /**
     * Gets the description of the item.
     *
     * @return the item description
     */
    @Nullable
    String getDescription();

    /**
     * Sets the description of the item.
     *
     * @param description the item description
     */
    void setDescription(@Nullable String description);

    /**
     * Gets the item stack of the item.
     *
     * @return the item stack
     */
    @Nullable
    ItemStack getItemStack();

    /**
     * Sets the item stack of the item.
     *
     * @param itemStack the item stack
     */
    void setItemStack(@Nullable ItemStack itemStack);

    /**
     * Gets the name of the item.
     *
     * @return the item name
     */
    @Nullable
    String getName();

    /**
     * Sets the name of the item.
     *
     * @param name the item name
     */
    void setName(@Nullable String name);

    /**
     * Gets whether a given {@link ItemStack} matches with the item. Always returns false if the item has no item stack
     * assigned.
     *
     * @param itemStack the item stack
     * @return whether it matches with the item's item stack
     */
    boolean isMatching(@NotNull ItemStack itemStack);
}
