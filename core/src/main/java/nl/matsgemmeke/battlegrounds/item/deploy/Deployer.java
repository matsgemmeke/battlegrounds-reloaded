package nl.matsgemmeke.battlegrounds.item.deploy;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents an entity that is capable of deploying items.
 */
public interface Deployer {

    /**
     * Gets the {@link ItemStack} that the entity is holding.
     *
     * @return the held item stack
     */
    @NotNull
    ItemStack getHeldItem();

    /**
     * Removes an {@link ItemStack} from the entity.
     *
     * @param itemStack the item stack to remove
     */
    void removeItem(@NotNull ItemStack itemStack);

    /**
     * Sets the {@link ItemStack} that the entity is holding.
     *
     * @param itemStack the item stack
     */
    void setHeldItem(@Nullable ItemStack itemStack);
}
