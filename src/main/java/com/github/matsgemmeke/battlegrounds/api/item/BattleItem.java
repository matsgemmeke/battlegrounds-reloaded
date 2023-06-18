package com.github.matsgemmeke.battlegrounds.api.item;

import com.github.matsgemmeke.battlegrounds.api.entity.BattleEntity;
import com.github.matsgemmeke.battlegrounds.api.game.BattleContext;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An item object used in Battlegrounds game modes.
 */
public interface BattleItem {

    /**
     * Gets the {@link BattleContext} the item is situated in.
     *
     * @return the item {@link BattleContext}
     */
    @NotNull
    BattleContext getContext();

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
    BattleEntity getHolder();

    /**
     * Sets the holder of the item.
     *
     * @param holder the item holder
     */
    void setHolder(@Nullable BattleEntity holder);

    /**
     * Gets the id of the item.
     *
     * @return the item id
     */
    @NotNull
    String getId();

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
     * Handles a performed left click on the item.
     *
     * @param battleEntity the entity who left-clicked the item
     */
    void onLeftClick(@NotNull BattleEntity battleEntity);

    /**
     * Handles a performed right click on the item.
     *
     * @param battleEntity the entity who right-clicked the item
     */
    void onRightClick(@NotNull BattleEntity battleEntity);
}
