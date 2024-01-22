package com.github.matsgemmeke.battlegrounds.event.handler;

import com.github.matsgemmeke.battlegrounds.api.GameProvider;
import com.github.matsgemmeke.battlegrounds.api.entity.GamePlayer;
import com.github.matsgemmeke.battlegrounds.api.game.Game;
import com.github.matsgemmeke.battlegrounds.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.jetbrains.annotations.NotNull;

public class EntityPickupItemEventHandler implements EventHandler<EntityPickupItemEvent> {

    @NotNull
    private GameProvider gameProvider;

    public EntityPickupItemEventHandler(@NotNull GameProvider gameProvider) {
        this.gameProvider = gameProvider;
    }

    public void handle(@NotNull EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        Game game = gameProvider.getGame(player);

        if (game == null) {
            return;
        }

        GamePlayer gamePlayer = game.getGamePlayer(player);

        if (gamePlayer == null) {
            return;
        }

        game.onPickupItem(gamePlayer, event);
    }
}
