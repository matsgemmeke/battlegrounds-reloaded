package nl.matsgemmeke.battlegrounds.game.component;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DefaultAudioEmitter implements AudioEmitter {

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
}
