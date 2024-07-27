package nl.matsgemmeke.battlegrounds.game.component;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.Game;
import nl.matsgemmeke.battlegrounds.item.ItemBehavior;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DefaultActionHandler implements ActionHandler {

    @NotNull
    private EntityRegistry<GamePlayer, Player> playerRegistry;
    @NotNull
    private Game game;

    public DefaultActionHandler(@NotNull Game game, @NotNull EntityRegistry<GamePlayer, Player> playerRegistry) {
        this.game = game;
        this.playerRegistry = playerRegistry;
    }

    public boolean handleItemChange(@NotNull Player player, @Nullable ItemStack changeFrom, @Nullable ItemStack changeTo) {
        GamePlayer gamePlayer = playerRegistry.findByUUID(player.getUniqueId());

        if (gamePlayer == null) {
            return true;
        }

        boolean performAction = true;

        for (ItemBehavior behavior : game.getItemBehaviors()) {
            if (changeFrom != null) {
                performAction = performAction & behavior.handleChangeFromAction(gamePlayer, changeFrom);
            }

            if (changeTo != null) {
                performAction = performAction & behavior.handleChangeToAction(gamePlayer, changeTo);
            }
        }

        return performAction;
    }

    public boolean handleItemDrop(@NotNull Player player, @NotNull ItemStack droppedItem) {
        GamePlayer gamePlayer = playerRegistry.findByUUID(player.getUniqueId());

        if (gamePlayer == null) {
            return true;
        }

        boolean performAction = true;

        for (ItemBehavior behavior : game.getItemBehaviors()) {
            performAction = performAction & behavior.handleDropItemAction(gamePlayer, droppedItem);
        }

        return performAction;
    }

    public boolean handleItemLeftClick(@NotNull Player player, @NotNull ItemStack clickedItem) {
        GamePlayer gamePlayer = playerRegistry.findByUUID(player.getUniqueId());

        if (gamePlayer == null) {
            return true;
        }

        boolean performAction = true;

        for (ItemBehavior behavior : game.getItemBehaviors()) {
            performAction = performAction & behavior.handleLeftClickAction(gamePlayer, clickedItem);
        }

        return performAction;
    }

    public boolean handleItemPickup(@NotNull Player player, @NotNull ItemStack pickupItem) {
        GamePlayer gamePlayer = playerRegistry.findByUUID(player.getUniqueId());

        if (gamePlayer == null) {
            return true;
        }

        boolean performAction = true;

        for (ItemBehavior behavior : game.getItemBehaviors()) {
            performAction = performAction & behavior.handlePickupItemAction(gamePlayer, pickupItem);
        }

        return performAction;
    }

    public boolean handleItemRightClick(@NotNull Player player, @NotNull ItemStack clickedItem) {
        GamePlayer gamePlayer = playerRegistry.findByUUID(player.getUniqueId());

        if (gamePlayer == null) {
            return true;
        }

        boolean performAction = true;

        for (ItemBehavior behavior : game.getItemBehaviors()) {
            performAction = performAction & behavior.handleRightClickAction(gamePlayer, clickedItem);
        }

        return performAction;
    }

    public boolean handleItemSwap(@NotNull Player player, @Nullable ItemStack swapFrom, @Nullable ItemStack swapTo) {
        GamePlayer gamePlayer = playerRegistry.findByUUID(player.getUniqueId());

        if (gamePlayer == null) {
            return true;
        }

        boolean performAction = true;

        for (ItemBehavior behavior : game.getItemBehaviors()) {
            if (swapFrom != null) {
                performAction = performAction & behavior.handleSwapFromAction(gamePlayer, swapFrom);
            }

            if (swapTo != null) {
                performAction = performAction & behavior.handleSwapToAction(gamePlayer, swapTo);
            }
        }

        return performAction;
    }
}
