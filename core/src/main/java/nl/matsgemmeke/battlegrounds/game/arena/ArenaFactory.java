package nl.matsgemmeke.battlegrounds.game.arena;

import com.google.inject.Inject;
import jakarta.inject.Named;
import nl.matsgemmeke.battlegrounds.game.arena.settings.ArenaSettings;
import nl.matsgemmeke.battlegrounds.game.configuration.*;
import nl.matsgemmeke.battlegrounds.game.mapper.ArenaSettingsMapper;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.InputStream;

/**
 * Factory class for creating {@link Arena} instances.
 */
public class ArenaFactory {

    private final ArenaSettingsConfigurationFactory arenaSettingsConfigurationFactory;
    private final ArenaSettingsMapper arenaSettingsMapper;
    private final ArenaSetupConfigurationFactory arenaSetupConfigurationFactory;
    private final File arenasFolder;
    private final Plugin plugin;

    @Inject
    public ArenaFactory(
            ArenaSettingsConfigurationFactory arenaSettingsConfigurationFactory,
            ArenaSettingsMapper arenaSettingsMapper,
            ArenaSetupConfigurationFactory arenaSetupConfigurationFactory,
            @Named("ArenasFolder") File arenasFolder,
            Plugin plugin
    ) {
        this.arenaSettingsConfigurationFactory = arenaSettingsConfigurationFactory;
        this.arenaSettingsMapper = arenaSettingsMapper;
        this.arenaSetupConfigurationFactory = arenaSetupConfigurationFactory;
        this.arenasFolder = arenasFolder;
        this.plugin = plugin;
    }

    /**
     * Creates a new {@link Arena} instance.
     *
     * @param id       the arena id
     * @param settings the arena settings
     * @return         a new arena instance
     */
    public Arena create(int id, ArenaSettings settings) {
        File arenaFolder = new File(arenasFolder, "arena-" + id);
        File settingsFile = new File(arenaFolder, "settings.yml");
        File setupFile = new File(arenaFolder, "setup.yml");

        InputStream settingsResource = plugin.getResource("arenas/settings.yml");
        ArenaSettingsSpec spec = arenaSettingsMapper.toSpec(settings);

        ArenaSettingsConfiguration settingsConfiguration = arenaSettingsConfigurationFactory.create(settingsFile, settingsResource);
        settingsConfiguration.load();
        settingsConfiguration.saveArenaSettings(spec);

        ArenaSetupConfiguration setupConfiguration = arenaSetupConfigurationFactory.create(setupFile);
        setupConfiguration.load();
        setupConfiguration.save();

        return new Arena(id, settings);
    }
}
