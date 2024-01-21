package com.github.matsgemmeke.battlegrounds.game;

import com.github.matsgemmeke.battlegrounds.api.entity.BattlePlayer;
import com.github.matsgemmeke.battlegrounds.api.game.Game;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractGame implements Game {

    @NotNull
    public abstract Iterable<BattlePlayer> getPlayers();

    @Nullable
    public BattlePlayer getBattlePlayer(@NotNull Player player) {
        for (BattlePlayer battlePlayer : this.getPlayers()) {
            if (battlePlayer.getEntity() == player) {
                return battlePlayer;
            }
        }
        return null;
    }

    public boolean hasPlayer(@NotNull Player player) {
        return this.getBattlePlayer(player) != null;
    }
}
