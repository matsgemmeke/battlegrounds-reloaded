package nl.matsgemmeke.battlegrounds.game;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DefaultGameSound implements GameSound {

    private float pitch;
    private float volume;
    private long delay;
    @NotNull
    private Sound sound;

    public DefaultGameSound(@NotNull Sound sound, float volume, float pitch, long delay) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
        this.delay = delay;
    }

    @NotNull
    public static GameSound parseSound(@NotNull String arg) {
        String[] parts = arg.split("-");

        Sound sound;
        float volume;
        float pitch;
        long delay;

        try {
            sound = Sound.valueOf(parts[0]);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unable to parse sound value " + parts[0]);
        }

        try {
            volume = Float.parseFloat(parts[1]);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unable to parse volume value " + parts[1]);
        }

        try {
            pitch = Float.parseFloat(parts[2]);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unable to parse pitch value " + parts[2]);
        }

        try {
            delay = Long.parseLong(parts[3]);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unable to parse delay value " + parts[3]);
        }

        return new DefaultGameSound(sound, volume, pitch, delay);
    }

    @NotNull
    public static List<GameSound> parseSounds(@Nullable String arg) {
        if (arg == null) {
            return Collections.emptyList();
        }

        List<GameSound> list = new ArrayList<>();
        String[] sounds = arg.split(", ");

        for (String sound : sounds) {
            list.add(parseSound(sound));
        }

        return list;
    }

    public long getDelay() {
        return delay;
    }

    public float getPitch() {
        return pitch;
    }

    @NotNull
    public Sound getSound() {
        return sound;
    }

    public float getVolume() {
        return volume;
    }
}
