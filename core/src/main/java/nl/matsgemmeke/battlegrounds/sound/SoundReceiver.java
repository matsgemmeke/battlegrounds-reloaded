package nl.matsgemmeke.battlegrounds.sound;

import org.bukkit.Location;

/**
 * Represents something that can perceive sounds.
 */
public interface SoundReceiver {

    /**
     * Plays a given sound cue to the receiver at a given location.
     *
     * @param location the location to play the sound
     * @param soundCue the sound cue to play
     */
    void playSound(Location location, SoundCue soundCue);
}
