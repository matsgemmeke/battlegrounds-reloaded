package nl.matsgemmeke.battlegrounds.sound;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import nl.matsgemmeke.battlegrounds.configuration.ConfigurationFile;
import nl.matsgemmeke.battlegrounds.sound.api.SoundService;
import nl.matsgemmeke.battlegrounds.sound.configuration.SoundsConfiguration;
import nl.matsgemmeke.battlegrounds.sound.configuration.SoundsConfigurationFileProvider;
import nl.matsgemmeke.battlegrounds.sound.mapper.SoundSequenceMapper;

public class SoundModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(BukkitSoundRegistry.class).in(Singleton.class);
        binder.bind(SoundsConfiguration.class).in(Singleton.class);
        binder.bind(SoundSequenceMapper.class).in(Singleton.class);
        binder.bind(SoundService.class).in(Singleton.class);

        binder.bind(ConfigurationFile.class).annotatedWith(Names.named("sounds")).toProvider(SoundsConfigurationFileProvider.class).in(Singleton.class);
    }
}
