package com.github.matsgemmeke.battlegrounds.event.handler;

import com.github.matsgemmeke.battlegrounds.api.BattleContextProvider;
import com.github.matsgemmeke.battlegrounds.api.entity.BattlePlayer;
import com.github.matsgemmeke.battlegrounds.api.game.BattleContext;
import com.github.matsgemmeke.battlegrounds.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerDropItemEventHandler implements EventHandler<PlayerDropItemEvent> {

    @NotNull
    private BattleContextProvider contextProvider;

    public PlayerDropItemEventHandler(@NotNull BattleContextProvider contextProvider) {
        this.contextProvider = contextProvider;
    }

    public void handle(@NotNull PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        BattleContext context = contextProvider.getContext(player);

        if (context == null) {
            return;
        }

        BattlePlayer battlePlayer = context.getBattlePlayer(player);

        if (battlePlayer == null) {
            return;
        }

        context.onItemDrop(battlePlayer, event);
    }
}
