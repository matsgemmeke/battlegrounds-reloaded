package nl.matsgemmeke.battlegrounds.sound.api;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.sound.configuration.SoundsConfiguration;
import nl.matsgemmeke.battlegrounds.sound.mapper.SoundTemplateMapper;

import java.util.Optional;

public class SoundService {

    private final SoundsConfiguration soundsConfiguration;
    private final SoundTemplateMapper soundTemplateMapper;

    @Inject
    public SoundService(SoundsConfiguration soundsConfiguration, SoundTemplateMapper soundTemplateMapper) {
        this.soundsConfiguration = soundsConfiguration;
        this.soundTemplateMapper = soundTemplateMapper;
    }

    public Optional<SoundTemplate> getSoundTemplate(String templateId) {
        return soundsConfiguration.getSoundTemplate(templateId).map(soundTemplateMapper::toSoundTemplate);
    }
}
