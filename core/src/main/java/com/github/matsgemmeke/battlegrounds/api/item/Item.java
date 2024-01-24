package com.github.matsgemmeke.battlegrounds.api.item;

import com.github.matsgemmeke.battlegrounds.api.entity.ItemHolder;
import com.github.matsgemmeke.battlegrounds.api.game.GameContext;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An item object that appears in game instances.
 */
public interface Item {

    /**
     * Gets the game context the item is situated in.
     *
     * @return the item's game context
     */
    @NotNull
    GameContext getContext();

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
     * Gets the holder of the item. Returns null if the item does not have a holder.
     *
     * @return the item holder or null if it does not have one
     */
    @Nullable
    ItemHolder getHolder();

    /**
     * Sets the holder of the item.
     *
     * @param holder the item holder
     */
    void setHolder(@Nullable ItemHolder holder);

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

    /**
     * Handles a performed held item change action on the item.
     *
     * @param holder the entity who changed the held item
     */
    void onChangeHeldItem(@NotNull ItemHolder holder);

    /**
     * Handles a performed drop on the item.
     *
     * @param holder the entity who dropped the item
     */
    void onDrop(@NotNull ItemHolder holder);

    /**
     * Handles a performed left click on the item.
     *
     * @param holder the entity who left-clicked the item
     */
    void onLeftClick(@NotNull ItemHolder holder);

    /**
     * Handles a performed right click on the item.
     *
     * @param holder the entity who right-clicked the item
     */
    void onRightClick(@NotNull ItemHolder holder);
}
