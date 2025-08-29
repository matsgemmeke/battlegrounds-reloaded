package nl.matsgemmeke.battlegrounds.game.component;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.Game;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.item.ActionExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DefaultActionHandler implements ActionHandler {

    @NotNull
    private Game game;
    @NotNull
    private PlayerRegistry playerRegistry;

    public DefaultActionHandler(@NotNull Game game, @NotNull PlayerRegistry playerRegistry) {
        this.game = game;
        this.playerRegistry = playerRegistry;
    }

    public boolean handleItemChange(@NotNull Player player, @Nullable ItemStack changeFrom, @Nullable ItemStack changeTo) {
        GamePlayer gamePlayer = playerRegistry.findByUUID(player.getUniqueId());

        if (gamePlayer == null) {
            return true;
        }

        boolean performAction = true;

        for (ActionExecutor actionExecutor : game.getActionExecutors()) {
            if (changeFrom != null) {
                performAction = performAction & actionExecutor.handleChangeFromAction(player, changeFrom);
            }

            if (changeTo != null) {
                performAction = performAction & actionExecutor.handleChangeToAction(player, changeTo);
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

        for (ActionExecutor actionExecutor : game.getActionExecutors()) {
            performAction = performAction & actionExecutor.handleDropItemAction(player, droppedItem);
        }

        return performAction;
    }

    public boolean handleItemLeftClick(@NotNull Player player, @NotNull ItemStack clickedItem) {
        GamePlayer gamePlayer = playerRegistry.findByUUID(player.getUniqueId());

        if (gamePlayer == null) {
            return true;
        }

        boolean performAction = true;

        for (ActionExecutor actionExecutor : game.getActionExecutors()) {
            performAction = performAction & actionExecutor.handleLeftClickAction(player, clickedItem);
        }

        return performAction;
    }

    public boolean handleItemPickup(@NotNull Player player, @NotNull ItemStack pickupItem) {
        GamePlayer gamePlayer = playerRegistry.findByUUID(player.getUniqueId());

        if (gamePlayer == null) {
            return true;
        }

        boolean performAction = true;

        for (ActionExecutor actionExecutor : game.getActionExecutors()) {
            performAction = performAction & actionExecutor.handlePickupItemAction(player, pickupItem);
        }

        return performAction;
    }

    public boolean handleItemRightClick(@NotNull Player player, @NotNull ItemStack clickedItem) {
        GamePlayer gamePlayer = playerRegistry.findByUUID(player.getUniqueId());

        if (gamePlayer == null) {
            return true;
        }

        boolean performAction = true;

        for (ActionExecutor actionExecutor : game.getActionExecutors()) {
            performAction = performAction & actionExecutor.handleRightClickAction(player, clickedItem);
        }

        return performAction;
    }

    public boolean handleItemSwap(@NotNull Player player, @Nullable ItemStack swapFrom, @Nullable ItemStack swapTo) {
        GamePlayer gamePlayer = playerRegistry.findByUUID(player.getUniqueId());

        if (gamePlayer == null) {
            return true;
        }

        boolean performAction = true;

        for (ActionExecutor actionExecutor : game.getActionExecutors()) {
            if (swapFrom != null) {
                performAction = performAction & actionExecutor.handleSwapFromAction(player, swapFrom);
            }

            if (swapTo != null) {
                performAction = performAction & actionExecutor.handleSwapToAction(player, swapTo);
            }
        }

        return performAction;
    }
}
