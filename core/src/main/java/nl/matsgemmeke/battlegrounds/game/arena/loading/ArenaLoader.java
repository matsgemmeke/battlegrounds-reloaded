package nl.matsgemmeke.battlegrounds.game.arena.loading;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.arena.Arena;
import nl.matsgemmeke.battlegrounds.game.arena.settings.ArenaSettings;
import nl.matsgemmeke.battlegrounds.game.configuration.ArenaSettingsConfiguration;
import nl.matsgemmeke.battlegrounds.game.configuration.ArenaSettingsConfigurationFactory;
import nl.matsgemmeke.battlegrounds.game.configuration.ArenaSettingsSpec;
import nl.matsgemmeke.battlegrounds.game.configuration.InvalidArenaSettingsSpecException;
import nl.matsgemmeke.battlegrounds.game.mapper.ArenaSettingsMapper;
import nl.matsgemmeke.battlegrounds.util.ResourceProvider;

import java.io.File;
import java.io.InputStream;

/**
 * Responsible for loading in a single arena.
 */
public class ArenaLoader {

    private static final String ARENA_SETTINGS_FILE_NAME = "settings.yml";
    private static final String ARENA_SETTINGS_RESOURCE = "arenas/settings.yml";

    private final ArenaSettingsConfigurationFactory arenaSettingsConfigurationFactory;
    private final ArenaSettingsMapper arenaSettingsMapper;
    private final GameContextProvider gameContextProvider;
    private final ResourceProvider resourceProvider;

    @Inject
    public ArenaLoader(
            ArenaSettingsConfigurationFactory arenaSettingsConfigurationFactory,
            ArenaSettingsMapper arenaSettingsMapper,
            GameContextProvider gameContextProvider,
            ResourceProvider resourceProvider
    ) {
        this.arenaSettingsConfigurationFactory = arenaSettingsConfigurationFactory;
        this.arenaSettingsMapper = arenaSettingsMapper;
        this.gameContextProvider = gameContextProvider;
        this.resourceProvider = resourceProvider;
    }

    public void loadArena(int arenaId, File arenaFolder) {
        GameKey gameKey = GameKey.ofArena(arenaId);

        File settingsFile = new File(arenaFolder, ARENA_SETTINGS_FILE_NAME);
        InputStream settingsResource = resourceProvider.getResource(ARENA_SETTINGS_RESOURCE);

        ArenaSettingsConfiguration settingsConfiguration = arenaSettingsConfigurationFactory.create(settingsFile, settingsResource);
        settingsConfiguration.load();

        ArenaSettingsSpec settingsSpec = this.getArenaSettingsSpec(arenaId, settingsConfiguration);
        ArenaSettings settings = arenaSettingsMapper.toDomain(settingsSpec);
        Arena arena = new Arena(arenaId, settings);

        gameContextProvider.addArena(gameKey, arena);
    }

    private ArenaSettingsSpec getArenaSettingsSpec(int arenaId, ArenaSettingsConfiguration settingsConfiguration) {
        try {
            return settingsConfiguration.getArenaSettings();
        } catch (InvalidArenaSettingsSpecException ex) {
            throw new InvalidArenaSetupException("Failed to load setup for arena %s".formatted(arenaId), ex);
        }
    }
}
