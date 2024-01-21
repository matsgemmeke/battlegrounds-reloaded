package com.github.matsgemmeke.battlegrounds.event.handler;

import com.github.matsgemmeke.battlegrounds.api.GameProvider;
import com.github.matsgemmeke.battlegrounds.api.entity.BattlePlayer;
import com.github.matsgemmeke.battlegrounds.api.game.Game;
import com.github.matsgemmeke.battlegrounds.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
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

        // Stop the method if the player is not in a battlegrounds context
        if (game == null) {
            return;
        }

        BattlePlayer battlePlayer = game.getBattlePlayer(player);

        // Stop if the BattlePlayer instance can not be found
        if (battlePlayer == null) {
            return;
        }

        game.onInteract(battlePlayer, event);
    }
}
