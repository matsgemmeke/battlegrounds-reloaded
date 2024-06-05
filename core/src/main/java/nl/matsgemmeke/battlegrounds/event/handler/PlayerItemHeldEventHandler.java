package nl.matsgemmeke.battlegrounds.event.handler;

import nl.matsgemmeke.battlegrounds.GameProvider;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.event.EventHandler;
import nl.matsgemmeke.battlegrounds.game.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PlayerItemHeldEventHandler implements EventHandler<PlayerItemHeldEvent> {

    @NotNull
    private GameProvider gameProvider;

    public PlayerItemHeldEventHandler(@NotNull GameProvider gameProvider) {
        this.gameProvider = gameProvider;
    }

    public void handle(@NotNull PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        Game game = gameProvider.getGame(player);

        // Stop the method if the player is not in a battlegrounds game
        if (game == null) {
            return;
        }

        GamePlayer gamePlayer = game.getGamePlayer(player);

        // Stop if the GamePlayer instance can not be found
        if (gamePlayer == null) {
            return;
        }

        ItemStack changeFrom = player.getInventory().getItemInMainHand();
        ItemStack changeTo = player.getInventory().getItem(event.getNewSlot());

        boolean performEvent = game.handleItemChange(gamePlayer, changeFrom, changeTo);

        event.setCancelled(event.isCancelled() || !performEvent);
    }
}
