package com.github.matsgemmeke.battlegrounds.api.entity;

import com.github.matsgemmeke.battlegrounds.api.game.TeamMember;
import com.github.matsgemmeke.battlegrounds.api.item.BattleItem;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an entity that is capable of holding and operating a {@link BattleItem}.
 */
public interface BattleItemHolder extends BattleEntity, TeamMember {

    /**
     * Applies effects to the holder for when they are operating an item.
     *
     * @param operating whether to apply the state for operating an item or not
     */
    void applyOperatingState(boolean operating);

    /**
     * Gets the relative accuracy based on the state of the entity. For example, it should return 1.0 if the
     * shooting accuracy is unaffected, 0.5 if the accuracy is worsened, 2.0 if the accuracy is improved etc.
     *
     * @return the relative shooting accuracy
     */
    double getRelativeAccuracy();

    /**
     * Updates the {@link ItemStack} of a {@link BattleItem} in the holder's inventory.
     *
     * @param itemStack the item
     * @return whether the item was updated
     */
    boolean updateItemStack(@NotNull ItemStack itemStack);
}
