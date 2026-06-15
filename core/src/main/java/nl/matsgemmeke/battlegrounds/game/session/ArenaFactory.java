package nl.matsgemmeke.battlegrounds.game.session;

import com.google.inject.Inject;
import jakarta.inject.Named;
import nl.matsgemmeke.battlegrounds.configuration.SessionDataConfiguration;

import java.io.File;

/**
 * Factory class for creating {@link Arena} instances.
 */
public class ArenaFactory {

    private final File setupFolder;

    @Inject
    public ArenaFactory(@Named("SetupFolder") File setupFolder) {
        this.setupFolder = setupFolder;
    }

    /**
     * Creates a new {@link Arena} instance.
     *
     * @param id the session id
     * @param configuration the session configuration
     * @return a new session instance
     */
    public Arena create(int id, ArenaConfiguration configuration) {
        File arenaConfigFile = new File(setupFolder.getPath() + "/arenas-" + id + "/config.yml");

        SessionDataConfiguration dataConfig = new SessionDataConfiguration(arenaConfigFile);
        dataConfig.load();
        dataConfig.saveConfiguration(configuration);

        return new Arena(configuration);
    }
}
