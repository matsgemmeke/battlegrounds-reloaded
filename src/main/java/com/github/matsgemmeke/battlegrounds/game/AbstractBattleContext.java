package com.github.matsgemmeke.battlegrounds.game;

import com.github.matsgemmeke.battlegrounds.TaskRunner;
import com.github.matsgemmeke.battlegrounds.api.entity.BattlePlayer;
import com.github.matsgemmeke.battlegrounds.api.game.BattleContext;
import com.github.matsgemmeke.battlegrounds.api.game.BattleSound;
import com.github.matsgemmeke.battlegrounds.entity.DefaultBattlePlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractBattleContext implements BattleContext {

    @NotNull
    private List<BattlePlayer> players;
    @NotNull
    private TaskRunner taskRunner;

    public AbstractBattleContext(@NotNull TaskRunner taskRunner) {
        this.taskRunner = taskRunner;
        this.players = new ArrayList<>();
    }

    @NotNull
    public BattlePlayer addPlayer(@NotNull Player player) {
        BattlePlayer battlePlayer = new DefaultBattlePlayer(player);

        players.add(battlePlayer);

        return battlePlayer;
    }

    @Nullable
    public BattlePlayer getBattlePlayer(@NotNull Player player) {
        for (BattlePlayer battlePlayer : players) {
            if (battlePlayer.getEntity() == player) {
                return battlePlayer;
            }
        }
        return null;
    }

    public boolean hasPlayer(@NotNull Player player) {
        return this.getBattlePlayer(player) != null;
    }

    public void playSound(@NotNull BattleSound sound, @NotNull Location location) {
        long delay = sound.getDelay();

        if (delay > 0) {
            taskRunner.runTaskLater(() -> this.playSoundToAllPlayers(sound, location), delay);
        } else {
            this.playSoundToAllPlayers(sound, location);
        }
    }

    public void playSounds(@NotNull Iterable<BattleSound> sounds, @NotNull Location location) {
        for (BattleSound sound : sounds) {
            this.playSound(sound, location);
        }
    }

    private void playSoundToAllPlayers(@NotNull BattleSound sound, @NotNull Location location) {
        for (BattlePlayer battlePlayer : players) {
            Player player = battlePlayer.getEntity();
            player.playSound(location, sound.getSound(), sound.getVolume(), sound.getPitch());
        }
    }
}
