package nl.matsgemmeke.battlegrounds.event.handler;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.event.EventHandler;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.ActionHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PlayerSwapHandItemsEventHandler implements EventHandler<PlayerSwapHandItemsEvent> {

    @NotNull
    private final GameContextProvider contextProvider;

    @Inject
    public PlayerSwapHandItemsEventHandler(@NotNull GameContextProvider contextProvider) {
        this.contextProvider = contextProvider;
    }

    public void handle(@NotNull PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        GameKey gameKey = contextProvider.getGameKey(player);

        if (gameKey == null) {
            return;
        }

        ActionHandler actionHandler = contextProvider.getComponent(gameKey, ActionHandler.class);

        ItemStack swapFrom = event.getOffHandItem();
        ItemStack swapTo = event.getMainHandItem();

        boolean performAction = actionHandler.handleItemSwap(player, swapFrom, swapTo);

        event.setCancelled(event.isCancelled() || !performAction);
    }
}
