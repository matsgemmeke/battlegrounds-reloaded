package nl.matsgemmeke.battlegrounds.event.handler;

import nl.matsgemmeke.battlegrounds.GameProvider;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.event.EventHandler;
import nl.matsgemmeke.battlegrounds.game.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PlayerSwapHandItemsEventHandler implements EventHandler<PlayerSwapHandItemsEvent> {

    @NotNull
    private GameProvider gameProvider;

    public PlayerSwapHandItemsEventHandler(@NotNull GameProvider gameProvider) {
        this.gameProvider = gameProvider;
    }

    public void handle(@NotNull PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        Game game = gameProvider.getGame(player);

        if (game == null) {
            return;
        }

        GamePlayer gamePlayer = game.getGamePlayer(player);

        if (gamePlayer == null) {
            return;
        }

        ItemStack swapFrom = event.getOffHandItem();
        ItemStack swapTo = event.getMainHandItem();

        boolean performAction = game.handleItemSwap(gamePlayer, swapFrom, swapTo);

        event.setCancelled(event.isCancelled() || !performAction);
    }
}
