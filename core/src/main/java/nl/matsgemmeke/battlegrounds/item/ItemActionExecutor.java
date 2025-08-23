package nl.matsgemmeke.battlegrounds.item;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Object that handles interactions performed on items and initiates behavior.
 */
public interface ItemActionExecutor {

    /**
     * Handles logic for when a player changes its held item from the given item to another item.
     *
     * @param gamePlayer the player
     * @param changedItem the item stack that the player is changing from
     * @return whether the action should be performed
     */
    boolean handleChangeFromAction(@NotNull GamePlayer gamePlayer, @NotNull ItemStack changedItem);

    /**
     * Handles logic for when a player changes its held item from another item to the given item.
     *
     * @param gamePlayer the player
     * @param changedItem the item stack that the player is changing from
     * @return whether the action should be performed
     */
    boolean handleChangeToAction(@NotNull GamePlayer gamePlayer, @NotNull ItemStack changedItem);

    /**
     * Handles logic for when a player drops an item.
     *
     * @param gamePlayer the player
     * @param droppedItem the item stack that was dropped
     * @return whether the action should be performed
     */
    boolean handleDropItemAction(@NotNull GamePlayer gamePlayer, @NotNull ItemStack droppedItem);

    /**
     * Handles logic for when a player left-clicks an item.
     *
     * @param gamePlayer the player
     * @param clickedItem the item stack that was clicked
     * @return whether the action should be performed
     */
    boolean handleLeftClickAction(@NotNull GamePlayer gamePlayer, @NotNull ItemStack clickedItem);

    /**
     * Handles logic for when a player picks up an item.
     *
     * @param gamePlayer the player
     * @param pickupItem the item stack that was picked up
     * @return whether the action should be performed
     */
    boolean handlePickupItemAction(@NotNull GamePlayer gamePlayer, @NotNull ItemStack pickupItem);

    /**
     * Handles logic for when a player right-clicks an item.
     *
     * @param gamePlayer the player
     * @param clickedItem the item stack that was clicked
     * @return whether the action should be performed
     */
    boolean handleRightClickAction(@NotNull GamePlayer gamePlayer, @NotNull ItemStack clickedItem);

    /**
     * Handles logic for when a player swaps an item away.
     *
     * @param gamePlayer the player
     * @param swappedItem the item stack that is being swapped from
     * @return whether the action should be performed
     */
    boolean handleSwapFromAction(@NotNull GamePlayer gamePlayer, @NotNull ItemStack swappedItem);

    /**
     * Handles logic for when a player swaps to an item.
     *
     * @param gamePlayer the player
     * @param swappedItem the item stack that is being swapped to
     * @return whether the action should be performed
     */
    boolean handleSwapToAction(@NotNull GamePlayer gamePlayer, @NotNull ItemStack swappedItem);
}
