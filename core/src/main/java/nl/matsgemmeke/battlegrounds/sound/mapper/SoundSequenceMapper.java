package nl.matsgemmeke.battlegrounds.sound.mapper;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.sound.BukkitSoundRegistry;
import nl.matsgemmeke.battlegrounds.sound.api.SoundCue;
import nl.matsgemmeke.battlegrounds.sound.api.SoundSequence;
import nl.matsgemmeke.battlegrounds.sound.api.SoundSequenceEntry;
import nl.matsgemmeke.battlegrounds.sound.configuration.SoundSequenceEntrySpec;
import nl.matsgemmeke.battlegrounds.sound.configuration.SoundSequenceSpec;
import org.bukkit.Sound;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class SoundSequenceMapper {

    private final BukkitSoundRegistry bukkitSoundRegistry;

    @Inject
    public SoundSequenceMapper(BukkitSoundRegistry bukkitSoundRegistry) {
        this.bukkitSoundRegistry = bukkitSoundRegistry;
    }

    public SoundSequence toSoundSequence(SoundSequenceSpec spec) {
        String id = spec.id();
        List<SoundSequenceEntry> entries = spec.entries().stream()
                .map(this::toSoundSequenceEntry)
                .filter(Objects::nonNull)
                .toList();

        return new SoundSequence(id, entries);
    }

    @Nullable
    private SoundSequenceEntry toSoundSequenceEntry(SoundSequenceEntrySpec spec) {
        String soundValue = spec.sound();
        Sound sound = bukkitSoundRegistry.getSound(soundValue).orElse(null);

        if (sound == null) {
            return null;
        }

        float volume = spec.volume();
        float pitch = spec.pitch();
        SoundCue soundCue = new SoundCue(sound, volume, pitch);
        int delay = spec.delay();

        return new SoundSequenceEntry(soundCue, delay);
    }
}
