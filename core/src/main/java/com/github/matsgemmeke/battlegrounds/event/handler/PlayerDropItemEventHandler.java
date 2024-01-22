package com.github.matsgemmeke.battlegrounds.event.handler;

import com.github.matsgemmeke.battlegrounds.api.GameProvider;
import com.github.matsgemmeke.battlegrounds.api.entity.GamePlayer;
import com.github.matsgemmeke.battlegrounds.api.game.Game;
import com.github.matsgemmeke.battlegrounds.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
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

        game.onItemDrop(gamePlayer, event);
    }
}
