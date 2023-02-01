package com.github.matsgemmeke.battlegrounds.game;

import com.github.matsgemmeke.battlegrounds.api.game.BattleSound;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DefaultBattleSound implements BattleSound {

    private float pitch;
    private float volume;
    private long delay;
    @NotNull
    private Sound sound;

    public DefaultBattleSound(@NotNull Sound sound, float volume, float pitch, long delay) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
        this.delay = delay;
    }

    public static BattleSound parseSound(@NotNull String arg) {
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

        return new DefaultBattleSound(sound, volume, pitch, delay);
    }

    public static List<BattleSound> parseSounds(@NotNull String arg) {
        List<BattleSound> list = new ArrayList<>();
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
