package nl.matsgemmeke.battlegrounds.event.handler;

import nl.matsgemmeke.battlegrounds.GameProvider;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.event.EventHandler;
import nl.matsgemmeke.battlegrounds.game.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PlayerInteractEventHandler implements EventHandler<PlayerInteractEvent> {

    @NotNull
    private GameProvider gameProvider;

    public PlayerInteractEventHandler(@NotNull GameProvider gameProvider) {
        this.gameProvider = gameProvider;
    }

    public void handle(@NotNull PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Game game = gameProvider.getGame(player);

        if (game == null) {
            return;
        }

        GamePlayer gamePlayer = game.getGamePlayer(player);

        if (gamePlayer == null) {
            return;
        }

        ItemStack itemStack = event.getItem();

        if (itemStack == null) {
            return;
        }

        boolean performAction = true;

        Action action = event.getAction();

        if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
            performAction = game.handleItemLeftClick(gamePlayer, itemStack);
        } else if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            performAction = game.handleItemRightClick(gamePlayer, itemStack);
        }

        event.setUseItemInHand(event.useItemInHand() == Result.DENY || !performAction ? Result.DENY : event.useItemInHand());
    }
}
