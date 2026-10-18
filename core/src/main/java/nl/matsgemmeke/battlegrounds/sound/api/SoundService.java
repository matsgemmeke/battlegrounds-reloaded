package nl.matsgemmeke.battlegrounds.sound.api;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.sound.configuration.SoundsConfiguration;
import nl.matsgemmeke.battlegrounds.sound.mapper.SoundSequenceMapper;

import java.util.Optional;

public class SoundService {

    private final SoundsConfiguration soundsConfiguration;
    private final SoundSequenceMapper soundSequenceMapper;

    @Inject
    public SoundService(SoundsConfiguration soundsConfiguration, SoundSequenceMapper soundSequenceMapper) {
        this.soundsConfiguration = soundsConfiguration;
        this.soundSequenceMapper = soundSequenceMapper;
    }

    public Optional<SoundSequence> getSoundSequence(String sequenceId) {
        return soundsConfiguration.getSoundSequence(sequenceId).map(soundSequenceMapper::toSoundSequence);
    }
}
