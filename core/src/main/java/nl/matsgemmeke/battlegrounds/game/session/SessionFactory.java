package nl.matsgemmeke.battlegrounds.game.session;

import nl.matsgemmeke.battlegrounds.InternalsProvider;
import nl.matsgemmeke.battlegrounds.configuration.SessionDataConfiguration;
import nl.matsgemmeke.battlegrounds.item.ItemStorage;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunHolder;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Factory class for creating {@link Session} instances.
 */
public class SessionFactory {

    @NotNull
    private File dataDirectory;
    @NotNull
    private InternalsProvider internals;

    public SessionFactory(@NotNull File dataDirectory, @NotNull InternalsProvider internals) {
        this.dataDirectory = dataDirectory;
        this.internals = internals;
    }

    /**
     * Makes a new {@link Session} instance.
     *
     * @param id the session id
     * @param configuration the session configuration
     * @return a new session instance
     */
    public Session make(int id, SessionConfiguration configuration) {
        File dataConfigFile = new File(dataDirectory.getPath() + "/session_" + id + "/config.yml");

        SessionDataConfiguration dataConfig = new SessionDataConfiguration(dataConfigFile);
        dataConfig.load();
        dataConfig.saveConfiguration(configuration);

        ItemStorage<Gun, GunHolder> gunStorage = new ItemStorage<>();

        return new DefaultSession(id, configuration, internals, gunStorage);
    }
}
