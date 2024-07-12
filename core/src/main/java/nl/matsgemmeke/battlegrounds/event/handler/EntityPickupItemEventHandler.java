package nl.matsgemmeke.battlegrounds.event.handler;

import nl.matsgemmeke.battlegrounds.event.EventHandler;
import nl.matsgemmeke.battlegrounds.game.access.ActionHandler;
import nl.matsgemmeke.battlegrounds.game.access.provider.ActionHandlerProvider;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class EntityPickupItemEventHandler implements EventHandler<EntityPickupItemEvent> {

    @NotNull
    private ActionHandlerProvider actionHandlerProvider;

    public EntityPickupItemEventHandler(@NotNull ActionHandlerProvider actionHandlerProvider) {
        this.actionHandlerProvider = actionHandlerProvider;
    }

    public void handle(@NotNull EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        ActionHandler actionHandler = actionHandlerProvider.getActionHandler(player);

        if (actionHandler == null) {
            return;
        }

        ItemStack itemStack = event.getItem().getItemStack();

        boolean performEvent = actionHandler.handleItemPickup(player, itemStack);

        event.setCancelled(event.isCancelled() || !performEvent);
    }
}
