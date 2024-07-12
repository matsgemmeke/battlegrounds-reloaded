package nl.matsgemmeke.battlegrounds.event.handler;

import nl.matsgemmeke.battlegrounds.event.EventHandler;
import nl.matsgemmeke.battlegrounds.game.access.ActionHandler;
import nl.matsgemmeke.battlegrounds.game.access.provider.ActionHandlerProvider;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PlayerItemHeldEventHandler implements EventHandler<PlayerItemHeldEvent> {

    @NotNull
    private ActionHandlerProvider actionHandlerProvider;

    public PlayerItemHeldEventHandler(@NotNull ActionHandlerProvider actionHandlerProvider) {
        this.actionHandlerProvider = actionHandlerProvider;
    }

    public void handle(@NotNull PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ActionHandler actionHandler = actionHandlerProvider.getActionHandler(player);

        // Stop the method if the player is not in a battlegrounds game
        if (actionHandler == null) {
            return;
        }

        ItemStack changeFrom = player.getInventory().getItemInMainHand();
        ItemStack changeTo = player.getInventory().getItem(event.getNewSlot());

        boolean performEvent = actionHandler.handleItemChange(player, changeFrom, changeTo);

        event.setCancelled(event.isCancelled() || !performEvent);
    }
}
