package com.github.matsgemmeke.battlegrounds.game;

import com.github.matsgemmeke.battlegrounds.TaskRunner;
import com.github.matsgemmeke.battlegrounds.api.entity.BattlePlayer;
import com.github.matsgemmeke.battlegrounds.api.game.BattleContext;
import com.github.matsgemmeke.battlegrounds.api.game.BattleSound;
import com.github.matsgemmeke.battlegrounds.item.BlockCollisionChecker;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractBattleContext implements BattleContext {

    @NotNull
    private BlockCollisionChecker collisionChecker;
    @NotNull
    private TaskRunner taskRunner;

    public AbstractBattleContext(@NotNull BlockCollisionChecker collisionChecker, @NotNull TaskRunner taskRunner) {
        this.collisionChecker = collisionChecker;
        this.taskRunner = taskRunner;
    }

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
        for (BattlePlayer battlePlayer : this.getPlayers()) {
            Player player = battlePlayer.getEntity();
            player.playSound(location, sound.getSound(), sound.getVolume(), sound.getPitch());
        }
    }

    public boolean producesCollisionAt(@NotNull Location location) {
        return collisionChecker.isSolid(location.getBlock(), location);
    }
}
