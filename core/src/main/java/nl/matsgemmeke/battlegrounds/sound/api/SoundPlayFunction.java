package nl.matsgemmeke.battlegrounds.sound.api;

import org.bukkit.Location;

@FunctionalInterface
interface SoundPlayFunction {

    void playSound(Location location, SoundCue soundCue);
}
