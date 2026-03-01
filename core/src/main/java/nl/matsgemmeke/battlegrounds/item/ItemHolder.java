package nl.matsgemmeke.battlegrounds.item;

import org.bukkit.inventory.ItemStack;

import java.util.Optional;

/**
 * Represents an entity that is capable of holding and operating a {@link Item}.
 */
public interface ItemHolder {

    /**
     * Gets the {@link ItemStack} that the item holder is holding.
     *
     * @return the held item stack
     */
    ItemStack getHeldItem();

    /**
     * Sets the {@link ItemStack} that the item holder is holding.
     *
     * @param itemStack the item stack
     */
    void setHeldItem(ItemStack itemStack);

    /**
     * Gets the item slot in which a given matchable item is located in the user's inventory.
     *
     * @param item the matchable item
     * @return     an optional containing the item slot, or an empty optional when the item cannot be found
     */
    Optional<Integer> getItemSlot(Matchable item);

    /**
     * Gets whether the holder carries a given item.
     *
     * @param item the item
     * @return     whether the holder carries the item
     */
    boolean hasItem(Matchable item);

    /**
     * Sets a given item into the holder's inventory with the given item slot.
     *
     * @param slot      the item slot number
     * @param itemStack the item stack
     */
    void setItem(int slot, ItemStack itemStack);
}
