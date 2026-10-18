package nl.matsgemmeke.battlegrounds.sound.mapper;

import nl.matsgemmeke.battlegrounds.extension.BukkitSoundRegistryExtension;
import nl.matsgemmeke.battlegrounds.sound.SoundSequence;
import nl.matsgemmeke.battlegrounds.sound.bukkit.BukkitSoundRegistry;
import nl.matsgemmeke.battlegrounds.sound.configuration.SoundSequenceEntrySpec;
import nl.matsgemmeke.battlegrounds.sound.configuration.SoundSequenceSpec;
import org.bukkit.Sound;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith({ MockitoExtension.class, BukkitSoundRegistryExtension.class })
class SoundSequenceMapperTest {

    private static final String ID = "assault-rifle-reload-1";
    private static final String SOUND = "ui.button.click";
    private static final String INVALID_SOUND = "fail";
    private static final float VOLUME = 1.0f;
    private static final float PITCH = 0.5f;
    private static final int DELAY = 5;

    @Mock
    private BukkitSoundRegistry bukkitSoundRegistry;
    @InjectMocks
    private SoundSequenceMapper soundSequenceMapper;

    @Test
    @DisplayName("toSoundSequence returns SoundSequence with values from given specification")
    void toSoundSequence() {
        Sound sound = mock(Sound.class);
        SoundSequenceEntrySpec validEntry = new SoundSequenceEntrySpec(SOUND, VOLUME, PITCH, DELAY);
        SoundSequenceEntrySpec invalidEntry = new SoundSequenceEntrySpec(INVALID_SOUND, VOLUME, PITCH, DELAY);
        SoundSequenceSpec soundSequenceSpec = new SoundSequenceSpec(ID, List.of(validEntry, invalidEntry));

        when(bukkitSoundRegistry.getSound(SOUND)).thenReturn(Optional.of(sound));
        when(bukkitSoundRegistry.getSound(INVALID_SOUND)).thenReturn(Optional.empty());

        SoundSequence soundSequence = soundSequenceMapper.toSoundSequence(soundSequenceSpec);

        assertThat(soundSequence.id()).isEqualTo(ID);
        assertThat(soundSequence.entries()).satisfiesExactly(entry -> {
            assertThat(entry.soundCue()).satisfies(soundCue -> {
                assertThat(soundCue.sound()).isEqualTo(sound);
                assertThat(soundCue.volume()).isEqualTo(VOLUME);
                assertThat(soundCue.pitch()).isEqualTo(PITCH);
            });
            assertThat(entry.delay()).isEqualTo(DELAY);
        });
    }
}
