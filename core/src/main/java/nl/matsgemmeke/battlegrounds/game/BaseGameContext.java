package nl.matsgemmeke.battlegrounds.game;

import nl.matsgemmeke.battlegrounds.entity.GameItem;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class BaseGameContext implements GameContext {

    @NotNull
    private BlockCollisionChecker collisionChecker;

    public BaseGameContext(@NotNull BlockCollisionChecker collisionChecker) {
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

    public boolean producesCollisionAt(@NotNull Location location) {
        Block block = location.getBlock();

        return collisionChecker.isSolid(block, location);
    }
}
