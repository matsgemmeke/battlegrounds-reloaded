package com.github.matsgemmeke.battlegrounds.event.handler;

import com.github.matsgemmeke.battlegrounds.api.game.TrainingMode;
import com.github.matsgemmeke.battlegrounds.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerJoinEventHandler implements EventHandler<PlayerJoinEvent> {

    @NotNull
    private TrainingMode trainingMode;

    public PlayerJoinEventHandler(@NotNull TrainingMode trainingMode) {
        this.trainingMode = trainingMode;
    }

    public void handle(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();

        trainingMode.addPlayer(player);
    }
}
