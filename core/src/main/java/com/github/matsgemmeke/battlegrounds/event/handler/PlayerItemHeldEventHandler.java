package com.github.matsgemmeke.battlegrounds.event.handler;

import com.github.matsgemmeke.battlegrounds.api.GameProvider;
import com.github.matsgemmeke.battlegrounds.api.entity.GamePlayer;
import com.github.matsgemmeke.battlegrounds.api.game.Game;
import com.github.matsgemmeke.battlegrounds.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemHeldEvent;
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

        game.onItemHeld(gamePlayer, event);
    }
}
