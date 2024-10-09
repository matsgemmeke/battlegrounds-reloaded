package nl.matsgemmeke.battlegrounds.item;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
