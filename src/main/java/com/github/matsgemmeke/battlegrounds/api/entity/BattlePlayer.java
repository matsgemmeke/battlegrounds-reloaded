package com.github.matsgemmeke.battlegrounds.api.entity;

import com.github.matsgemmeke.battlegrounds.api.item.BattleItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * A player object which holds information and can perform actions in Battlegrounds.
 */
public interface BattlePlayer extends BattleEntity {

    /**
     * Gets the {@link Player} entity of the object.
     *
     * @return the player entity
     */
    @NotNull
    Player getEntity();

    /**
     * Gets the items the player is holding.
     *
     * @return the player items
     */
    @NotNull
    Set<BattleItem> getItems();

    /**
     * Attempts to find a {@link BattleItem} based on its {@link ItemStack}. Returns null if there were no matches.
     *
     * @param itemStack the item stack
     * @return the corresponding {@link BattleItem} or null if there were no matches
     */
    @Nullable
    BattleItem getBattleItem(@NotNull ItemStack itemStack);
}
