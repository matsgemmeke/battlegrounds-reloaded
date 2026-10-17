package nl.matsgemmeke.battlegrounds.sound.mapper;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.sound.BukkitSoundRegistry;
import nl.matsgemmeke.battlegrounds.sound.api.SoundCue;
import nl.matsgemmeke.battlegrounds.sound.api.SoundTemplate;
import nl.matsgemmeke.battlegrounds.sound.api.SoundTemplateEntry;
import nl.matsgemmeke.battlegrounds.sound.configuration.SoundTemplateEntrySpec;
import nl.matsgemmeke.battlegrounds.sound.configuration.SoundTemplateSpec;
import org.bukkit.Sound;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class SoundTemplateMapper {

    private final BukkitSoundRegistry bukkitSoundRegistry;

    @Inject
    public SoundTemplateMapper(BukkitSoundRegistry bukkitSoundRegistry) {
        this.bukkitSoundRegistry = bukkitSoundRegistry;
    }

    public SoundTemplate toSoundTemplate(SoundTemplateSpec spec) {
        String id = spec.id();
        List<SoundTemplateEntry> entries = spec.entries().stream()
                .map(this::toSoundTemplateEntry)
                .filter(Objects::nonNull)
                .toList();

        return new SoundTemplate(id, entries);
    }

    @Nullable
    private SoundTemplateEntry toSoundTemplateEntry(SoundTemplateEntrySpec spec) {
        String soundValue = spec.sound();
        Sound sound = bukkitSoundRegistry.getSound(soundValue).orElse(null);

        if (sound == null) {
            return null;
        }

        float volume = spec.volume();
        float pitch = spec.pitch();
        SoundCue soundCue = new SoundCue(sound, volume, pitch);
        int delay = spec.delay();

        return new SoundTemplateEntry(soundCue, delay);
    }
}
