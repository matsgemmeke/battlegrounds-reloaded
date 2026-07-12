package nl.matsgemmeke.battlegrounds.arena.configuration;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import nl.matsgemmeke.battlegrounds.configuration.yaml.YamlConfigurationFile;
import nl.matsgemmeke.battlegrounds.configuration.yaml.YamlConfigurationFileFactory;

import java.io.File;

public class ArenaSetupConfigurationFactory {

    private final File arenasFolder;
    private final YamlConfigurationFileFactory yamlConfigurationFileFactory;

    @Inject
    public ArenaSetupConfigurationFactory(@Named("ArenasFolder") File arenasFolder, YamlConfigurationFileFactory yamlConfigurationFileFactory) {
        this.arenasFolder = arenasFolder;
        this.yamlConfigurationFileFactory = yamlConfigurationFileFactory;
    }

    public ArenaSetupConfiguration create(int arenaId) {
        File arenaFolder = new File(arenasFolder, "arena-" + arenaId);
        File setupFile = new File(arenaFolder, "setup.yml");
        YamlConfigurationFile setupConfigurationFile = yamlConfigurationFileFactory.create(setupFile);

        return new ArenaSetupConfiguration(setupConfigurationFile);
    }
}
