package com.github.matsgemmeke.battlegrounds.event.handler;

import com.github.matsgemmeke.battlegrounds.api.BattleContextProvider;
import com.github.matsgemmeke.battlegrounds.api.entity.BattlePlayer;
import com.github.matsgemmeke.battlegrounds.api.game.BattleContext;
import com.github.matsgemmeke.battlegrounds.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerItemHeldEventHandler implements EventHandler<PlayerItemHeldEvent> {

    @NotNull
    private BattleContextProvider contextProvider;

    public PlayerItemHeldEventHandler(@NotNull BattleContextProvider contextProvider) {
        this.contextProvider = contextProvider;
    }

    public void handle(@NotNull PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        BattleContext context = contextProvider.getContext(player);

        // Stop the method if the player is not in a battlegrounds context
        if (context == null) {
            return;
        }

        BattlePlayer battlePlayer = context.getBattlePlayer(player);

        // Stop if the BattlePlayer instance can not be found
        if (battlePlayer == null) {
            return;
        }

        context.onItemHeld(battlePlayer, event);
    }
}
