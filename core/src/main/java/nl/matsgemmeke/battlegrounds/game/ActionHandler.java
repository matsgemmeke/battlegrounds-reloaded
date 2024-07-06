package nl.matsgemmeke.battlegrounds.game;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface ActionHandler {

    /**
     * Handles logic for when a player left-clicks an item.
     *
     * @param player the player
     * @param clickedItem the item stack that was clicked
     * @return whether the action should be performed
     */
    boolean handleItemLeftClick(@NotNull Player player, @NotNull ItemStack clickedItem);

    /**
     * Executes logic that handles right clicks by players.
     *
     * @param player the player
     * @param clickedItem the item that was clicked
     * @return whether the action should be performed
     */
    boolean handleItemRightClick(@NotNull Player player, @NotNull ItemStack clickedItem);
}
