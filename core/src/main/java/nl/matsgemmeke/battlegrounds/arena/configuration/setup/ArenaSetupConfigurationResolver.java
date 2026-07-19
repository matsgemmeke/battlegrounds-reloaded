package nl.matsgemmeke.battlegrounds.arena.configuration.setup;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import nl.matsgemmeke.battlegrounds.configuration.yaml.YamlConfigurationFile;
import nl.matsgemmeke.battlegrounds.configuration.yaml.YamlConfigurationFileFactory;

import java.io.File;

/**
 * This is a separate factory class on top of {@link ArenaSetupConfigurationFactory}, since the construction of
 * {@link ArenaSetupConfiguration} requires both assisted variables and a custom provider setup.
 */
public class ArenaSetupConfigurationResolver {

    private final ArenaSetupConfigurationFactory arenaSetupConfigurationFactory;
    private final File arenasFolder;
    private final YamlConfigurationFileFactory yamlConfigurationFileFactory;

    @Inject
    public ArenaSetupConfigurationResolver(
            ArenaSetupConfigurationFactory arenaSetupConfigurationFactory,
            @Named("ArenasFolder") File arenasFolder,
            YamlConfigurationFileFactory yamlConfigurationFileFactory
    ) {
        this.arenaSetupConfigurationFactory = arenaSetupConfigurationFactory;
        this.arenasFolder = arenasFolder;
        this.yamlConfigurationFileFactory = yamlConfigurationFileFactory;
    }

    public ArenaSetupConfiguration resolve(int arenaId) {
        File arenaFolder = new File(arenasFolder, "arena-" + arenaId);
        File setupFile = new File(arenaFolder, "setup.yml");
        YamlConfigurationFile configurationFile = yamlConfigurationFileFactory.create(setupFile);

        return arenaSetupConfigurationFactory.create(configurationFile);
    }
}
