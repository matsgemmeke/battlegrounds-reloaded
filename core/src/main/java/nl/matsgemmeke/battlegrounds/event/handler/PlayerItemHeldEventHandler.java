package nl.matsgemmeke.battlegrounds.event.handler;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.event.EventHandler;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.ActionHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PlayerItemHeldEventHandler implements EventHandler<PlayerItemHeldEvent> {

    @NotNull
    private final GameContextProvider contextProvider;

    @Inject
    public PlayerItemHeldEventHandler(@NotNull GameContextProvider contextProvider) {
        this.contextProvider = contextProvider;
    }

    public void handle(@NotNull PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        GameKey gameKey = contextProvider.getGameKey(player);

        // Stop the method if the player is not in a battlegrounds game
        if (gameKey == null) {
            return;
        }

        ItemStack changeFrom = player.getInventory().getItemInMainHand();
        ItemStack changeTo = player.getInventory().getItem(event.getNewSlot());

        ActionHandler actionHandler = contextProvider.getComponent(gameKey, ActionHandler.class);

        boolean performEvent = actionHandler.handleItemChange(player, changeFrom, changeTo);

        event.setCancelled(event.isCancelled() || !performEvent);
    }
}
