package nl.matsgemmeke.battlegrounds.sound.api;

import nl.matsgemmeke.battlegrounds.sound.configuration.SoundSequenceSpec;
import nl.matsgemmeke.battlegrounds.sound.configuration.SoundsConfiguration;
import nl.matsgemmeke.battlegrounds.sound.mapper.SoundSequenceMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SoundServiceTest {

    private static final String SEQUENCE_ID = "assault-rifle-reload-1";

    @Mock
    private SoundsConfiguration soundsConfiguration;
    @Mock
    private SoundSequenceMapper soundSequenceMapper;
    @InjectMocks
    private SoundService soundService;

    @Test
    @DisplayName("getSoundSequence returns empty optional when given sequence id does not exist in configuration")
    void getSoundSequence_sequenceIdNotExists() {
        when(soundsConfiguration.getSoundSequence(SEQUENCE_ID)).thenReturn(Optional.empty());

        Optional<SoundSequence> soundSequenceOptional = soundService.getSoundSequence(SEQUENCE_ID);

        assertThat(soundSequenceOptional).isEmpty();
    }

    @Test
    @DisplayName("getSoundSequence returns optional with converted sound sequence")
    void getSoundSequence_successful() {
        SoundSequenceSpec soundSequenceSpec = mock(SoundSequenceSpec.class);
        SoundSequence soundSequence = mock(SoundSequence.class);

        when(soundsConfiguration.getSoundSequence(SEQUENCE_ID)).thenReturn(Optional.of(soundSequenceSpec));
        when(soundSequenceMapper.toSoundSequence(soundSequenceSpec)).thenReturn(soundSequence);

        Optional<SoundSequence> soundSequenceOptional = soundService.getSoundSequence(SEQUENCE_ID);

        assertThat(soundSequenceOptional).hasValue(soundSequence);
    }
}
