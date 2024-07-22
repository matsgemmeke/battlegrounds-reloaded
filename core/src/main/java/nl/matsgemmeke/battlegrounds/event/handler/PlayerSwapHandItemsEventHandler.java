package nl.matsgemmeke.battlegrounds.event.handler;

import nl.matsgemmeke.battlegrounds.GameContextProvider;
import nl.matsgemmeke.battlegrounds.event.EventHandler;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.access.ActionHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PlayerSwapHandItemsEventHandler implements EventHandler<PlayerSwapHandItemsEvent> {

    @NotNull
    private GameContextProvider contextProvider;

    public PlayerSwapHandItemsEventHandler(@NotNull GameContextProvider contextProvider) {
        this.contextProvider = contextProvider;
    }

    public void handle(@NotNull PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        GameContext context = contextProvider.getContext(player);

        if (context == null) {
            return;
        }

        ActionHandler actionHandler = context.getActionHandler();

        ItemStack swapFrom = event.getOffHandItem();
        ItemStack swapTo = event.getMainHandItem();

        boolean performAction = actionHandler.handleItemSwap(player, swapFrom, swapTo);

        event.setCancelled(event.isCancelled() || !performAction);
    }
}
