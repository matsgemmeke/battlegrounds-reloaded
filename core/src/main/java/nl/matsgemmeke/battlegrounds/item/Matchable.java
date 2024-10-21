package nl.matsgemmeke.battlegrounds.item;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * An item that can be matched with an {@link ItemStack}.
 */
public interface Matchable {

    /**
     * Gets whether a given {@link ItemStack} matches with the item.
     *
     * @param itemStack the item stack
     * @return whether the given item stack matches with the item
     */
    boolean isMatching(@NotNull ItemStack itemStack);
}
