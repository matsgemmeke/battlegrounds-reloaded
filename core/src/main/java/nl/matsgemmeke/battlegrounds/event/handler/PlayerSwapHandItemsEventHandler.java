package nl.matsgemmeke.battlegrounds.event.handler;

import nl.matsgemmeke.battlegrounds.event.EventHandler;
import nl.matsgemmeke.battlegrounds.game.access.ActionHandler;
import nl.matsgemmeke.battlegrounds.game.access.provider.ActionHandlerProvider;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PlayerSwapHandItemsEventHandler implements EventHandler<PlayerSwapHandItemsEvent> {

    @NotNull
    private ActionHandlerProvider actionHandlerProvider;

    public PlayerSwapHandItemsEventHandler(@NotNull ActionHandlerProvider actionHandlerProvider) {
        this.actionHandlerProvider = actionHandlerProvider;
    }

    public void handle(@NotNull PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        ActionHandler actionHandler = actionHandlerProvider.getActionHandler(player);

        if (actionHandler == null) {
            return;
        }

        ItemStack swapFrom = event.getOffHandItem();
        ItemStack swapTo = event.getMainHandItem();

        boolean performAction = actionHandler.handleItemSwap(player, swapFrom, swapTo);

        event.setCancelled(event.isCancelled() || !performAction);
    }
}
