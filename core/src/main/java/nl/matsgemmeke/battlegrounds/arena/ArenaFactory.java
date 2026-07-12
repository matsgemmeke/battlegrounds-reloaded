package nl.matsgemmeke.battlegrounds.arena;

import com.google.inject.Inject;
import jakarta.inject.Named;
import nl.matsgemmeke.battlegrounds.arena.configuration.*;
import nl.matsgemmeke.battlegrounds.arena.mapper.ArenaSettingsMapper;
import nl.matsgemmeke.battlegrounds.arena.settings.ArenaSettings;
import nl.matsgemmeke.battlegrounds.configuration.yaml.YamlConfigurationFile;
import nl.matsgemmeke.battlegrounds.configuration.yaml.YamlConfigurationFileFactory;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.InputStream;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

/**
 * Factory class for creating {@link Arena} instances.
 */
public class ArenaFactory {

    private final ArenaSettingsConfigurationFactory arenaSettingsConfigurationFactory;
    private final ArenaSettingsMapper arenaSettingsMapper;
    private final ArenaSetupConfigurationFactory arenaSetupConfigurationFactory;
    private final Clock clock;
    private final File arenasFolder;
    private final Plugin plugin;
    private final YamlConfigurationFileFactory yamlConfigurationFileFactory;

    @Inject
    public ArenaFactory(
            ArenaSettingsConfigurationFactory arenaSettingsConfigurationFactory,
            ArenaSettingsMapper arenaSettingsMapper,
            ArenaSetupConfigurationFactory arenaSetupConfigurationFactory,
            Clock clock,
            @Named("ArenasFolder") File arenasFolder,
            Plugin plugin,
            YamlConfigurationFileFactory yamlConfigurationFileFactory
    ) {
        this.arenaSettingsConfigurationFactory = arenaSettingsConfigurationFactory;
        this.arenaSettingsMapper = arenaSettingsMapper;
        this.arenaSetupConfigurationFactory = arenaSetupConfigurationFactory;
        this.clock = clock;
        this.arenasFolder = arenasFolder;
        this.plugin = plugin;
        this.yamlConfigurationFileFactory = yamlConfigurationFileFactory;
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
        File arenaFolder = new File(arenasFolder, "arena-" + id);

        // Create settings.yml file
        File settingsFile = new File(arenaFolder, "settings.yml");
        InputStream settingsResource = plugin.getResource("arenas/settings.yml");
        YamlConfigurationFile settingsConfigurationFile = yamlConfigurationFileFactory.create(settingsFile, settingsResource);

        ArenaSettingsSpec spec = arenaSettingsMapper.toSpec(settings);

        ArenaSettingsConfiguration settingsConfiguration = arenaSettingsConfigurationFactory.create(settingsConfigurationFile);
        settingsConfiguration.saveArenaSettings(spec);

        // Create setup.yml file
        ArenaSetupConfiguration setupConfiguration = arenaSetupConfigurationFactory.create(id);
        setupConfiguration.setCreatedAt(Instant.now(clock));
        setupConfiguration.setCreatedBy(createdBy);
        setupConfiguration.save();

        return new Arena(id, settings);
    }
}
