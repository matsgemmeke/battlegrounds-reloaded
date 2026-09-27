package nl.matsgemmeke.battlegrounds.configuration.data;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import nl.matsgemmeke.battlegrounds.configuration.ConfigurationFile;
import nl.matsgemmeke.battlegrounds.configuration.yaml.YamlConfigurationFile;

import java.io.File;

public class DataConfigurationFileProvider implements Provider<ConfigurationFile> {

    private final File dataFolder;

    @Inject
    public DataConfigurationFileProvider(@Named("DataFolder") File dataFolder) {
        this.dataFolder = dataFolder;
    }

    @Override
    public ConfigurationFile get() {
        File dataFile = new File(dataFolder.getAbsoluteFile(), "data.yml");

        YamlConfigurationFile configurationFile = new YamlConfigurationFile(dataFile);
        configurationFile.load();
        return configurationFile;
    }
}
