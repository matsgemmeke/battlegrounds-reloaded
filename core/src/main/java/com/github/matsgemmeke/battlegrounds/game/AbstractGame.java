package com.github.matsgemmeke.battlegrounds.game;

import com.github.matsgemmeke.battlegrounds.api.entity.GamePlayer;
import com.github.matsgemmeke.battlegrounds.api.game.Game;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractGame implements Game {

    @NotNull
    public abstract Iterable<GamePlayer> getPlayers();

    @Nullable
    public GamePlayer getGamePlayer(@NotNull Player player) {
        for (GamePlayer gamePlayer : this.getPlayers()) {
            if (gamePlayer.getEntity() == player) {
                return gamePlayer;
            }
        }
        return null;
    }

    public boolean hasPlayer(@NotNull Player player) {
        return this.getGamePlayer(player) != null;
    }
}
