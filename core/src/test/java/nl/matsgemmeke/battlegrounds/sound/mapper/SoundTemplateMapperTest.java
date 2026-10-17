package nl.matsgemmeke.battlegrounds.sound.mapper;

import nl.matsgemmeke.battlegrounds.extension.BukkitSoundRegistryExtension;
import nl.matsgemmeke.battlegrounds.sound.BukkitSoundRegistry;
import nl.matsgemmeke.battlegrounds.sound.api.SoundTemplate;
import nl.matsgemmeke.battlegrounds.sound.configuration.SoundTemplateEntrySpec;
import nl.matsgemmeke.battlegrounds.sound.configuration.SoundTemplateSpec;
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
class SoundTemplateMapperTest {

    private static final String ID = "assault-rifle-reload-1";
    private static final String SOUND = "ui.button.click";
    private static final String INVALID_SOUND = "fail";
    private static final float VOLUME = 1.0f;
    private static final float PITCH = 0.5f;
    private static final int DELAY = 5;

    @Mock
    private BukkitSoundRegistry bukkitSoundRegistry;
    @InjectMocks
    private SoundTemplateMapper soundTemplateMapper;

    @Test
    @DisplayName("toSoundTemplate returns SoundTemplate with values from given specification")
    void toSoundTemplate() {
        Sound sound = mock(Sound.class);
        SoundTemplateEntrySpec validEntry = new SoundTemplateEntrySpec(SOUND, VOLUME, PITCH, DELAY);
        SoundTemplateEntrySpec invalidEntry = new SoundTemplateEntrySpec(INVALID_SOUND, VOLUME, PITCH, DELAY);
        SoundTemplateSpec soundTemplateSpec = new SoundTemplateSpec(ID, List.of(validEntry, invalidEntry));

        when(bukkitSoundRegistry.getSound(SOUND)).thenReturn(Optional.of(sound));
        when(bukkitSoundRegistry.getSound(INVALID_SOUND)).thenReturn(Optional.empty());

        SoundTemplate soundTemplate = soundTemplateMapper.toSoundTemplate(soundTemplateSpec);

        assertThat(soundTemplate.id()).isEqualTo(ID);
        assertThat(soundTemplate.entries()).satisfiesExactly(entry -> {
            assertThat(entry.soundCue()).satisfies(soundCue -> {
                assertThat(soundCue.sound()).isEqualTo(sound);
                assertThat(soundCue.volume()).isEqualTo(VOLUME);
                assertThat(soundCue.pitch()).isEqualTo(PITCH);
            });
            assertThat(entry.delay()).isEqualTo(DELAY);
        });
    }
}
