package nl.matsgemmeke.battlegrounds.game.session;

import com.google.inject.Inject;
import jakarta.inject.Named;
import nl.matsgemmeke.battlegrounds.InternalsProvider;
import nl.matsgemmeke.battlegrounds.configuration.SessionDataConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Factory class for creating {@link Session} instances.
 */
public class SessionFactory {

    @NotNull
    private File setupFolder;
    @NotNull
    private InternalsProvider internals;

    @Inject
    public SessionFactory(@Named("SetupFolder") @NotNull File setupFolder, @NotNull InternalsProvider internals) {
        this.setupFolder = setupFolder;
        this.internals = internals;
    }

    /**
     * Makes a new {@link Session} instance.
     *
     * @param id the session id
     * @param configuration the session configuration
     * @return a new session instance
     */
    @NotNull
    public Session make(int id, SessionConfiguration configuration) {
        File sessionConfigFile = new File(setupFolder.getPath() + "/session-" + id + "/config.yml");

        SessionDataConfiguration dataConfig = new SessionDataConfiguration(sessionConfigFile);
        dataConfig.load();
        dataConfig.saveConfiguration(configuration);

        return new DefaultSession(id, configuration, internals);
    }
}
