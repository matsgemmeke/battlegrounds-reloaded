package nl.matsgemmeke.battlegrounds.sound.api;

import nl.matsgemmeke.battlegrounds.sound.configuration.SoundTemplateSpec;
import nl.matsgemmeke.battlegrounds.sound.configuration.SoundsConfiguration;
import nl.matsgemmeke.battlegrounds.sound.mapper.SoundTemplateMapper;
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

    private static final String TEMPLATE_ID = "assault-rifle-reload-1";

    @Mock
    private SoundsConfiguration soundsConfiguration;
    @Mock
    private SoundTemplateMapper soundTemplateMapper;
    @InjectMocks
    private SoundService soundService;

    @Test
    @DisplayName("getSoundTemplate returns empty optional when given template does not exist in configuration")
    void getSoundTemplate_templateIdNotExists() {
        when(soundsConfiguration.getSoundTemplate(TEMPLATE_ID)).thenReturn(Optional.empty());

        Optional<SoundTemplate> soundTemplateOptional = soundService.getSoundTemplate(TEMPLATE_ID);

        assertThat(soundTemplateOptional).isEmpty();
    }

    @Test
    @DisplayName("getSoundTemplate returns optional with converted sound template")
    void getSoundTemplate_successful() {
        SoundTemplateSpec soundTemplateSpec = mock(SoundTemplateSpec.class);
        SoundTemplate soundTemplate = mock(SoundTemplate.class);

        when(soundsConfiguration.getSoundTemplate(TEMPLATE_ID)).thenReturn(Optional.of(soundTemplateSpec));
        when(soundTemplateMapper.toSoundTemplate(soundTemplateSpec)).thenReturn(soundTemplate);

        Optional<SoundTemplate> soundTemplateOptional = soundService.getSoundTemplate(TEMPLATE_ID);

        assertThat(soundTemplateOptional).hasValue(soundTemplate);
    }
}
