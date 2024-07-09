package nl.matsgemmeke.battlegrounds.event.handler;

import nl.matsgemmeke.battlegrounds.event.EventHandler;
import nl.matsgemmeke.battlegrounds.game.ActionHandler;
import nl.matsgemmeke.battlegrounds.game.provider.ActionHandlerProvider;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PlayerDropItemEventHandler implements EventHandler<PlayerDropItemEvent> {

    @NotNull
    private ActionHandlerProvider actionHandlerProvider;

    public PlayerDropItemEventHandler(@NotNull ActionHandlerProvider actionHandlerProvider) {
        this.actionHandlerProvider = actionHandlerProvider;
    }

    public void handle(@NotNull PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ActionHandler actionHandler = actionHandlerProvider.getActionHandler(player);

        if (actionHandler == null) {
            return;
        }

        ItemStack itemStack = event.getItemDrop().getItemStack();

        boolean performAction = actionHandler.handleItemDrop(player, itemStack);

        event.setCancelled(event.isCancelled() || !performAction);
    }
}
