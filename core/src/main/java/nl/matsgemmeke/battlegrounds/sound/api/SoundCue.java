package nl.matsgemmeke.battlegrounds.sound.api;

import org.bukkit.Sound;

public record SoundCue(Sound sound, float volume, float pitch, int delay) {
}
