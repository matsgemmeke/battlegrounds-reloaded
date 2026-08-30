package nl.matsgemmeke.battlegrounds.arena.loading;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.arena.Arena;
import nl.matsgemmeke.battlegrounds.arena.ArenaRegistry;
import nl.matsgemmeke.battlegrounds.arena.configuration.settings.*;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.ArenaSetupConfiguration;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.ArenaSetupConfigurationProvider;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.map.ArenaMapData;
import nl.matsgemmeke.battlegrounds.arena.loading.element.CompositeElementFactory;
import nl.matsgemmeke.battlegrounds.arena.map.ArenaMap;
import nl.matsgemmeke.battlegrounds.arena.map.ArenaMapMetadata;
import nl.matsgemmeke.battlegrounds.arena.mapper.ArenaSettingsMapper;
import nl.matsgemmeke.battlegrounds.arena.settings.ArenaSettings;
import nl.matsgemmeke.battlegrounds.game.GameKey;

/**
 * Responsible for loading in a single arena.
 */
public class ArenaLoader {

    private final ArenaRegistry arenaRegistry;
    private final ArenaSettingsConfigurationProvider arenaSettingsConfigurationProvider;
    private final ArenaSettingsMapper arenaSettingsMapper;
    private final ArenaSetupConfigurationProvider arenaSetupConfigurationProvider;
    private final CompositeElementFactory compositeElementFactory;

    @Inject
    public ArenaLoader(
            ArenaRegistry arenaRegistry,
            ArenaSettingsConfigurationProvider arenaSettingsConfigurationProvider,
            ArenaSettingsMapper arenaSettingsMapper,
            ArenaSetupConfigurationProvider arenaSetupConfigurationProvider,
            CompositeElementFactory compositeElementFactory
    ) {
        this.arenaRegistry = arenaRegistry;
        this.arenaSettingsConfigurationProvider = arenaSettingsConfigurationProvider;
        this.arenaSettingsMapper = arenaSettingsMapper;
        this.arenaSetupConfigurationProvider = arenaSetupConfigurationProvider;
        this.compositeElementFactory = compositeElementFactory;
    }

    public void loadArena(int arenaId) {
        GameKey gameKey = GameKey.ofArena(arenaId);

        ArenaSettingsConfiguration settingsConfiguration = arenaSettingsConfigurationProvider.get(arenaId);
        ArenaSettingsSpec settingsSpec = this.getArenaSettingsSpec(arenaId, settingsConfiguration);
        ArenaSettings settings = arenaSettingsMapper.toDomain(settingsSpec);

        Arena arena = new Arena(arenaId, settings);

        ArenaSetupConfiguration setupConfiguration = arenaSetupConfigurationProvider.get(arenaId);
        setupConfiguration.getMaps().forEach(data -> this.loadMap(arena, data));

        arenaRegistry.addArena(gameKey, arena);
    }

    private ArenaSettingsSpec getArenaSettingsSpec(int arenaId, ArenaSettingsConfiguration settingsConfiguration) {
        try {
            return settingsConfiguration.getArenaSettings();
        } catch (InvalidArenaSettingsSpecException ex) {
            throw new InvalidArenaSetupException("Failed to load setup for arena %s".formatted(arenaId), ex);
        }
    }

    private void loadMap(Arena arena, ArenaMapData mapData) {
        String name = mapData.name();
        ArenaMapMetadata metadata = new ArenaMapMetadata(mapData.createdAt(), mapData.createdBy());

        ArenaMap map = new ArenaMap(name, metadata);

        mapData.elements().stream()
                .map(compositeElementFactory::create)
                .forEach(map::addElement);

        arena.addMap(map);
    }
}
