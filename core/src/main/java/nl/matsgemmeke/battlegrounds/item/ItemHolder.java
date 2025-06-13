package nl.matsgemmeke.battlegrounds.item;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Represents an entity that is capable of holding and operating a {@link Item}.
 */
public interface ItemHolder extends GameEntity {

    /**
     * Gets the {@link ItemStack} that the item holder is holding.
     *
     * @return the held item stack
     */
    @NotNull
    ItemStack getHeldItem();

    /**
     * Gets the item slot in which a given matchable item is located in the user's inventory.
     *
     * @param item the matchable item
     * @return     an optional containing the item slot, or an empty optional when the item cannot be found
     */
    @NotNull
    Optional<Integer> getItemSlot(@NotNull Matchable item);

    /**
     * Removes an {@link ItemStack} from the holder.
     *
     * @param itemStack the item stack to remove
     */
    void removeItem(@NotNull ItemStack itemStack);

    /**
     * Sets the {@link ItemStack} that the item holder is holding.
     *
     * @param itemStack the item stack
     */
    void setHeldItem(@Nullable ItemStack itemStack);
}
