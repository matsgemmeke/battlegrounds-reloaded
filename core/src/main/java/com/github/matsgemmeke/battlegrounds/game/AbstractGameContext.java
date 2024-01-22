package com.github.matsgemmeke.battlegrounds.game;

import com.github.matsgemmeke.battlegrounds.api.game.GameContext;
import com.github.matsgemmeke.battlegrounds.api.game.GameSound;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractGameContext implements GameContext {

    @NotNull
    private BlockCollisionChecker collisionChecker;

    public AbstractGameContext(@NotNull BlockCollisionChecker collisionChecker) {
        this.collisionChecker = collisionChecker;
    }

    public void playSound(@NotNull GameSound sound, @NotNull Location location) {
        World world = location.getWorld();

        if (world == null) {
            return;
        }

        // Only play the sound for players that are in the same world
        for (Player player : world.getPlayers()) {
            player.playSound(location, sound.getSound(), sound.getVolume(), sound.getPitch());
        }
    }

    public void playSounds(@NotNull Iterable<GameSound> sounds, @NotNull Location location) {
        for (GameSound sound : sounds) {
            this.playSound(sound, location);
        }
    }

    public boolean producesCollisionAt(@NotNull Block block, @NotNull Location location) {
        return collisionChecker.isSolid(block, location);
    }
}
