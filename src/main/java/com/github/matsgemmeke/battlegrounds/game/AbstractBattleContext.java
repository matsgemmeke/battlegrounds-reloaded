package com.github.matsgemmeke.battlegrounds.game;

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

    public AbstractBattleContext(@NotNull BlockCollisionChecker collisionChecker) {
        this.collisionChecker = collisionChecker;
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
        for (BattlePlayer battlePlayer : this.getPlayers()) {
            Player player = battlePlayer.getEntity();
            player.playSound(location, sound.getSound(), sound.getVolume(), sound.getPitch());
        }
    }

    public void playSounds(@NotNull Iterable<BattleSound> sounds, @NotNull Location location) {
        for (BattleSound sound : sounds) {
            this.playSound(sound, location);
        }
    }

    public boolean producesCollisionAt(@NotNull Location location) {
        return collisionChecker.isSolid(location.getBlock(), location);
    }
}
