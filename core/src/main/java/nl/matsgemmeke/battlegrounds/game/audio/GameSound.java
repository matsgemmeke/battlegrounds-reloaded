package nl.matsgemmeke.battlegrounds.game.audio;

import nl.matsgemmeke.battlegrounds.game.Game;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a sound produced inside a {@link Game} instance.
 */
public interface GameSound {

    /**
     * Gets the delay of the sound in ticks.
     *
     * @return the sound delay
     */
    long getDelay();

    /**
     * Gets the pitch of the sound.
     *
     * @return the sound pitch
     */
    float getPitch();

    /**
     * Gets the sound value.
     *
     * @return the sound value
     */
    @NotNull
    Sound getSound();

    /**
     * Gets the volume of the sound.
     *
     * @return the sound value
     */
    float getVolume();
}
