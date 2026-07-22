package nl.matsgemmeke.battlegrounds.arena.configuration.settings;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import nl.matsgemmeke.battlegrounds.configuration.yaml.YamlConfigurationFile;
import nl.matsgemmeke.battlegrounds.configuration.yaml.YamlConfigurationFileFactory;
import nl.matsgemmeke.battlegrounds.util.ResourceProvider;

import java.io.File;
import java.io.InputStream;

/**
 * This is a separate provider class on top of {@link ArenaSettingsConfigurationFactory}, since the construction of
 * {@link ArenaSettingsConfiguration} requires both assisted variables and a custom provider setup.
 */
public class ArenaSettingsConfigurationProvider {

    private static final String ARENA_SETTINGS_FILE_NAME = "settings.yml";
    private static final String ARENA_SETTINGS_RESOURCE = "arenas/settings.yml";

    private final ArenaSettingsConfigurationFactory arenaSettingsConfigurationFactory;
    private final File arenasFolder;
    private final ResourceProvider resourceProvider;
    private final YamlConfigurationFileFactory yamlConfigurationFileFactory;

    @Inject
    public ArenaSettingsConfigurationProvider(
            ArenaSettingsConfigurationFactory arenaSettingsConfigurationFactory,
            @Named("ArenasFolder") File arenasFolder,
            ResourceProvider resourceProvider,
            YamlConfigurationFileFactory yamlConfigurationFileFactory
    ) {
        this.arenaSettingsConfigurationFactory = arenaSettingsConfigurationFactory;
        this.arenasFolder = arenasFolder;
        this.resourceProvider = resourceProvider;
        this.yamlConfigurationFileFactory = yamlConfigurationFileFactory;
    }

    public ArenaSettingsConfiguration get(int arenaId) {
        File arenaFolder = new File(arenasFolder, "arena-" + arenaId);
        File settingsFile = new File(arenaFolder, ARENA_SETTINGS_FILE_NAME);
        InputStream settingsResource = resourceProvider.getResource(ARENA_SETTINGS_RESOURCE);
        YamlConfigurationFile configurationFile = yamlConfigurationFileFactory.create(settingsFile, settingsResource);

        return arenaSettingsConfigurationFactory.create(configurationFile);
    }
}
