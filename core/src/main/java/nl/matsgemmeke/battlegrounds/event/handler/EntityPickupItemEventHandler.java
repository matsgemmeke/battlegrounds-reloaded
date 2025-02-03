package nl.matsgemmeke.battlegrounds.event.handler;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.event.EventHandler;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.component.ActionHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class EntityPickupItemEventHandler implements EventHandler<EntityPickupItemEvent> {

    @NotNull
    private GameContextProvider contextProvider;

    @Inject
    public EntityPickupItemEventHandler(@NotNull GameContextProvider contextProvider) {
        this.contextProvider = contextProvider;
    }

    public void handle(@NotNull EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        GameContext context = contextProvider.getContext(player);

        if (context == null) {
            return;
        }

        ActionHandler actionHandler = context.getActionHandler();
        ItemStack itemStack = event.getItem().getItemStack();

        boolean performEvent = actionHandler.handleItemPickup(player, itemStack);

        event.setCancelled(event.isCancelled() || !performEvent);
    }
}
