package com.github.matsgemmeke.battlegrounds.event.handler;

import com.github.matsgemmeke.battlegrounds.api.BattleContextProvider;
import com.github.matsgemmeke.battlegrounds.api.entity.BattlePlayer;
import com.github.matsgemmeke.battlegrounds.api.game.BattleContext;
import com.github.matsgemmeke.battlegrounds.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.jetbrains.annotations.NotNull;

public class EntityPickupItemEventHandler implements EventHandler<EntityPickupItemEvent> {

    @NotNull
    private BattleContextProvider contextProvider;

    public EntityPickupItemEventHandler(@NotNull BattleContextProvider contextProvider) {
        this.contextProvider = contextProvider;
    }

    public void handle(@NotNull EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        BattleContext context = contextProvider.getContext(player);

        if (context == null) {
            return;
        }

        BattlePlayer battlePlayer = context.getBattlePlayer(player);

        if (battlePlayer == null) {
            return;
        }

        context.onPickupItem(battlePlayer, event);
    }
}
