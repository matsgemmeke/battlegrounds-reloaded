package nl.matsgemmeke.battlegrounds.event.handler;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.event.EventHandler;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.ActionHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PlayerDropItemEventHandler implements EventHandler<PlayerDropItemEvent> {

    @NotNull
    private final GameContextProvider contextProvider;

    @Inject
    public PlayerDropItemEventHandler(@NotNull GameContextProvider contextProvider) {
        this.contextProvider = contextProvider;
    }

    public void handle(@NotNull PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        GameKey gameKey = contextProvider.getGameKey(player);

        if (gameKey == null) {
            return;
        }

        ActionHandler actionHandler = contextProvider.getComponent(gameKey, ActionHandler.class);
        ItemStack itemStack = event.getItemDrop().getItemStack();

        boolean performAction = actionHandler.handleItemDrop(player, itemStack);

        event.setCancelled(event.isCancelled() || !performAction);
    }
}
