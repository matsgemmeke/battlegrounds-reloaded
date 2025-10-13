package nl.matsgemmeke.battlegrounds.item;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * An item object that appears in game instances.
 */
public interface Item extends Matchable {

    /**
     * Gets the unique id of the item.
     *
     * @return the item id
     */
    UUID getId();

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
    @NotNull
    String getName();

    /**
     * Sets the name of the item.
     *
     * @param name the item name
     */
    void setName(@NotNull String name);
}
