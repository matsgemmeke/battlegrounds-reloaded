package nl.matsgemmeke.battlegrounds.sound.configuration;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import nl.matsgemmeke.battlegrounds.configuration.ConfigurationFile;
import nl.matsgemmeke.battlegrounds.configuration.yaml.YamlConfigurationFile;
import nl.matsgemmeke.battlegrounds.configuration.yaml.YamlConfigurationFileFactory;
import nl.matsgemmeke.battlegrounds.util.ResourceProvider;

import java.io.File;
import java.io.InputStream;

public class SoundsConfigurationFileProvider implements Provider<ConfigurationFile> {

    private final File dataFolder;
    private final ResourceProvider resourceProvider;
    private final YamlConfigurationFileFactory yamlConfigurationFileFactory;

    @Inject
    public SoundsConfigurationFileProvider(@Named("dataFolder") File dataFolder, ResourceProvider resourceProvider, YamlConfigurationFileFactory yamlConfigurationFileFactory) {
        this.dataFolder = dataFolder;
        this.resourceProvider = resourceProvider;
        this.yamlConfigurationFileFactory = yamlConfigurationFileFactory;
    }

    @Override
    public ConfigurationFile get() {
        File soundsFile = new File(dataFolder.getAbsoluteFile(), "sounds.yml");
        InputStream soundsResource = resourceProvider.getResource("sounds.yml");

        YamlConfigurationFile configurationFile = yamlConfigurationFileFactory.create(soundsFile, soundsResource);
        configurationFile.load();

        return configurationFile;
    }
}
