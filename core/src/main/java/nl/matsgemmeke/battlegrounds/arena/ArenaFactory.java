package nl.matsgemmeke.battlegrounds.arena;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.arena.configuration.settings.ArenaSettingsConfiguration;
import nl.matsgemmeke.battlegrounds.arena.configuration.settings.ArenaSettingsConfigurationProvider;
import nl.matsgemmeke.battlegrounds.arena.configuration.settings.ArenaSettingsSpec;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.ArenaSetupConfiguration;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.ArenaSetupConfigurationProvider;
import nl.matsgemmeke.battlegrounds.arena.mapper.ArenaSettingsMapper;
import nl.matsgemmeke.battlegrounds.arena.settings.ArenaSettings;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

/**
 * Factory class for creating {@link Arena} instances.
 */
public class ArenaFactory {

    private final ArenaSettingsConfigurationProvider arenaSettingsConfigurationProvider;
    private final ArenaSettingsMapper arenaSettingsMapper;
    private final ArenaSetupConfigurationProvider arenaSetupConfigurationProvider;
    private final Clock clock;

    @Inject
    public ArenaFactory(
            ArenaSettingsConfigurationProvider arenaSettingsConfigurationProvider,
            ArenaSettingsMapper arenaSettingsMapper,
            ArenaSetupConfigurationProvider arenaSetupConfigurationProvider,
            Clock clock
    ) {
        this.arenaSettingsConfigurationProvider = arenaSettingsConfigurationProvider;
        this.arenaSettingsMapper = arenaSettingsMapper;
        this.arenaSetupConfigurationProvider = arenaSetupConfigurationProvider;
        this.clock = clock;
    }

    /**
     * Creates a new {@link Arena} instance.
     *
     * @param id        the arena id
     * @param settings  the arena settings
     * @param createdBy the unique id of the player who created the arena
     * @return          a new arena instance
     */
    public Arena create(int id, ArenaSettings settings, UUID createdBy) {
        // Create settings.yml file
        ArenaSettingsSpec spec = arenaSettingsMapper.toSpec(settings);

        ArenaSettingsConfiguration settingsConfiguration = arenaSettingsConfigurationProvider.get(id);
        settingsConfiguration.saveArenaSettings(spec);

        // Create setup.yml file
        ArenaSetupConfiguration setupConfiguration = arenaSetupConfigurationProvider.get(id);
        setupConfiguration.setCreatedAt(Instant.now(clock));
        setupConfiguration.setCreatedBy(createdBy);

        return new Arena(id, settings);
    }
}
