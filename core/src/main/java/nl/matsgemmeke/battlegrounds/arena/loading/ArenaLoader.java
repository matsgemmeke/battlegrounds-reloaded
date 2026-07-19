package nl.matsgemmeke.battlegrounds.arena.loading;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.arena.Arena;
import nl.matsgemmeke.battlegrounds.arena.ArenaRegistry;
import nl.matsgemmeke.battlegrounds.arena.configuration.settings.ArenaSettingsConfiguration;
import nl.matsgemmeke.battlegrounds.arena.configuration.settings.ArenaSettingsConfigurationFactory;
import nl.matsgemmeke.battlegrounds.arena.configuration.settings.ArenaSettingsSpec;
import nl.matsgemmeke.battlegrounds.arena.configuration.settings.InvalidArenaSettingsSpecException;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.ArenaMapData;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.ArenaSetupConfiguration;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.ArenaSetupConfigurationResolver;
import nl.matsgemmeke.battlegrounds.arena.map.ArenaMap;
import nl.matsgemmeke.battlegrounds.arena.mapper.ArenaSettingsMapper;
import nl.matsgemmeke.battlegrounds.arena.settings.ArenaSettings;
import nl.matsgemmeke.battlegrounds.configuration.yaml.YamlConfigurationFile;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.util.ResourceProvider;

import java.io.File;
import java.io.InputStream;

/**
 * Responsible for loading in a single arena.
 */
public class ArenaLoader {

    private static final String ARENA_SETTINGS_FILE_NAME = "settings.yml";
    private static final String ARENA_SETTINGS_RESOURCE = "arenas/settings.yml";

    private final ArenaRegistry arenaRegistry;
    private final ArenaSettingsConfigurationFactory arenaSettingsConfigurationFactory;
    private final ArenaSettingsMapper arenaSettingsMapper;
    private final ArenaSetupConfigurationResolver arenaSetupConfigurationResolver;
    private final ResourceProvider resourceProvider;

    @Inject
    public ArenaLoader(
            ArenaRegistry arenaRegistry,
            ArenaSettingsConfigurationFactory arenaSettingsConfigurationFactory,
            ArenaSettingsMapper arenaSettingsMapper,
            ArenaSetupConfigurationResolver arenaSetupConfigurationResolver,
            ResourceProvider resourceProvider
    ) {
        this.arenaRegistry = arenaRegistry;
        this.arenaSettingsConfigurationFactory = arenaSettingsConfigurationFactory;
        this.arenaSettingsMapper = arenaSettingsMapper;
        this.arenaSetupConfigurationResolver = arenaSetupConfigurationResolver;
        this.resourceProvider = resourceProvider;
    }

    public void loadArena(int arenaId, File arenaFolder) {
        GameKey gameKey = GameKey.ofArena(arenaId);

        File settingsFile = new File(arenaFolder, ARENA_SETTINGS_FILE_NAME);
        InputStream settingsResource = resourceProvider.getResource(ARENA_SETTINGS_RESOURCE);
        YamlConfigurationFile configurationFile = new YamlConfigurationFile(settingsFile, settingsResource);

        ArenaSettingsConfiguration settingsConfiguration = arenaSettingsConfigurationFactory.create(configurationFile);

        ArenaSettingsSpec settingsSpec = this.getArenaSettingsSpec(arenaId, settingsConfiguration);
        ArenaSettings settings = arenaSettingsMapper.toDomain(settingsSpec);
        Arena arena = new Arena(arenaId, settings);

        ArenaSetupConfiguration setupConfiguration = arenaSetupConfigurationResolver.resolve(arenaId);

        for (ArenaMapData mapData : setupConfiguration.getMaps()) {
            String name = mapData.name();
            ArenaMap map = new ArenaMap(name);

            arena.addMap(map);
        }

        arenaRegistry.addArena(gameKey, arena);
    }

    private ArenaSettingsSpec getArenaSettingsSpec(int arenaId, ArenaSettingsConfiguration settingsConfiguration) {
        try {
            return settingsConfiguration.getArenaSettings();
        } catch (InvalidArenaSettingsSpecException ex) {
            throw new InvalidArenaSetupException("Failed to load setup for arena %s".formatted(arenaId), ex);
        }
    }
}
