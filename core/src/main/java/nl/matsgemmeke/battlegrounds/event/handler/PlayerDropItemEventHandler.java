package nl.matsgemmeke.battlegrounds.event.handler;

import nl.matsgemmeke.battlegrounds.GameProvider;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.event.EventHandler;
import nl.matsgemmeke.battlegrounds.game.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PlayerDropItemEventHandler implements EventHandler<PlayerDropItemEvent> {

    @NotNull
    private GameProvider gameProvider;

    public PlayerDropItemEventHandler(@NotNull GameProvider gameProvider) {
        this.gameProvider = gameProvider;
    }

    public void handle(@NotNull PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Game game = gameProvider.getGame(player);

        if (game == null) {
            return;
        }

        GamePlayer gamePlayer = game.getGamePlayer(player);

        if (gamePlayer == null) {
            return;
        }

        ItemStack itemStack = event.getItemDrop().getItemStack();

        boolean performAction = game.handleItemDrop(gamePlayer, itemStack);

        event.setCancelled(event.isCancelled() || !performAction);
    }
}
