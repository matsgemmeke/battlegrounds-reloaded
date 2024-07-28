package nl.matsgemmeke.battlegrounds.game.component;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

/**
 * Object that is able to play audio to players inside games.
 */
public interface AudioEmitter {

    /**
     * Plays a {@link GameSound} for all players in the game.
     *
     * @param sound the sound
     * @param location the location to play the sound
     */
    void playSound(@NotNull GameSound sound, @NotNull Location location);

    /**
     * Plays multiple {@link GameSound}s for all players in the game.
     *
     * @param sound the sound
     * @param location the location to play the sound
     */
    void playSounds(@NotNull Iterable<GameSound> sound,@NotNull Location location);
}
