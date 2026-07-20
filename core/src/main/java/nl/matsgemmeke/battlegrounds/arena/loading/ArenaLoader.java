package nl.matsgemmeke.battlegrounds.arena.loading;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.arena.Arena;
import nl.matsgemmeke.battlegrounds.arena.ArenaRegistry;
import nl.matsgemmeke.battlegrounds.arena.configuration.settings.*;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.ArenaMapData;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.ArenaSetupConfiguration;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.ArenaSetupConfigurationResolver;
import nl.matsgemmeke.battlegrounds.arena.map.ArenaMap;
import nl.matsgemmeke.battlegrounds.arena.mapper.ArenaSettingsMapper;
import nl.matsgemmeke.battlegrounds.arena.settings.ArenaSettings;
import nl.matsgemmeke.battlegrounds.game.GameKey;

import java.io.File;

/**
 * Responsible for loading in a single arena.
 */
public class ArenaLoader {

    private final ArenaRegistry arenaRegistry;
    private final ArenaSettingsConfigurationProvider arenaSettingsConfigurationProvider;
    private final ArenaSettingsMapper arenaSettingsMapper;
    private final ArenaSetupConfigurationResolver arenaSetupConfigurationResolver;

    @Inject
    public ArenaLoader(
            ArenaRegistry arenaRegistry,
            ArenaSettingsConfigurationProvider arenaSettingsConfigurationProvider,
            ArenaSettingsMapper arenaSettingsMapper,
            ArenaSetupConfigurationResolver arenaSetupConfigurationResolver
    ) {
        this.arenaRegistry = arenaRegistry;
        this.arenaSettingsConfigurationProvider = arenaSettingsConfigurationProvider;
        this.arenaSettingsMapper = arenaSettingsMapper;
        this.arenaSetupConfigurationResolver = arenaSetupConfigurationResolver;
    }

    public void loadArena(int arenaId) {
        GameKey gameKey = GameKey.ofArena(arenaId);

        ArenaSettingsConfiguration settingsConfiguration = arenaSettingsConfigurationProvider.get(arenaId);
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
