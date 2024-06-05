package nl.matsgemmeke.battlegrounds.game.audio;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public interface AudioSource {

    /**
     * Gets the location of the where its audio would originate from.
     *
     * @return the audio play location
     */
    @NotNull
    Location getAudioPlayLocation();
}
