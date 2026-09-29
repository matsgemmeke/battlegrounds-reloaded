package nl.matsgemmeke.battlegrounds.location.configuration;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import nl.matsgemmeke.battlegrounds.configuration.ConfigurationFile;
import nl.matsgemmeke.battlegrounds.configuration.yaml.YamlConfigurationFile;

import java.io.File;

public class LocationConfigurationFileProvider implements Provider<ConfigurationFile> {

    private final File dataFolder;

    @Inject
    public LocationConfigurationFileProvider(@Named("dataFolder") File dataFolder) {
        this.dataFolder = dataFolder;
    }

    @Override
    public ConfigurationFile get() {
        File locationsFile = new File(dataFolder.getAbsoluteFile(), "locations.yml");

        YamlConfigurationFile configurationFile = new YamlConfigurationFile(locationsFile);
        configurationFile.load();
        return configurationFile;
    }
}
