package nl.matsgemmeke.battlegrounds.game.configuration;

import nl.matsgemmeke.battlegrounds.configuration.yaml.YamlConfigurationFile;

import java.io.File;

public class ArenaSetupConfigurationFactory {

    public ArenaSetupConfiguration create(File file) {
        YamlConfigurationFile configurationFile = new YamlConfigurationFile(file);
        configurationFile.load();

        return new ArenaSetupConfiguration(configurationFile);
    }
}
