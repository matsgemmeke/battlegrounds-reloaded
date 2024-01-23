package com.github.matsgemmeke.battlegrounds.api.entity;

import com.github.matsgemmeke.battlegrounds.api.game.TeamMember;
import com.github.matsgemmeke.battlegrounds.api.item.Item;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents an entity that is capable of holding and operating a {@link Item}.
 */
public interface ItemHolder extends GameEntity, TeamMember {

    /**
     * Adds a {@link Item} to the holder.
     *
     * @param item the item
     * @return whether the item was added
     */
    boolean addItem(@NotNull Item item);

    /**
     * Applies effects to the holder for when they are operating an item.
     *
     * @param operating whether to apply the state for operating an item or not
     */
    void applyOperatingState(boolean operating);

    /**
     * Attempts to find a {@link Item} based on its {@link ItemStack}. Returns null if there were no matches.
     *
     * @param itemStack the item stack
     * @return the corresponding item or null if there were no matches
     */
    @Nullable
    Item getItem(@NotNull ItemStack itemStack);

    /**
     * Gets the relative accuracy based on the state of the entity. For example, it should return 1.0 if the
     * shooting accuracy is unaffected, 0.5 if the accuracy is worsened, 2.0 if the accuracy is improved etc.
     *
     * @return the relative shooting accuracy
     */
    float getRelativeAccuracy();

    /**
     * Removes an item from the holder.
     *
     * @param item the item to remove
     * @return whether the item was removed
     */
    boolean removeItem(@NotNull Item item);

    /**
     * Updates the {@link ItemStack} of a {@link Item} in the holder's inventory.
     *
     * @param itemStack the item
     * @return whether the item was updated
     */
    boolean updateItemStack(@NotNull ItemStack itemStack);
}
