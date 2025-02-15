package nl.matsgemmeke.battlegrounds.event.handler;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.event.EventHandler;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.ActionHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PlayerInteractEventHandler implements EventHandler<PlayerInteractEvent> {

    @NotNull
    private final GameContextProvider contextProvider;

    @Inject
    public PlayerInteractEventHandler(@NotNull GameContextProvider contextProvider) {
        this.contextProvider = contextProvider;
    }

    public void handle(@NotNull PlayerInteractEvent event) {
        ItemStack itemStack = event.getItem();

        if (itemStack == null) {
            return;
        }

        Player player = event.getPlayer();
        GameKey gameKey = contextProvider.getGameKey(player);

        if (gameKey == null) {
            return;
        }

        boolean performAction = true;

        Action action = event.getAction();
        ActionHandler actionHandler = contextProvider.getComponent(gameKey, ActionHandler.class);

        if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
            performAction = actionHandler.handleItemLeftClick(player, itemStack);
        } else if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            performAction = actionHandler.handleItemRightClick(player, itemStack);
        }

        event.setUseItemInHand(event.useItemInHand() == Result.DENY || !performAction ? Result.DENY : event.useItemInHand());
    }
}
