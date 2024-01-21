package com.github.matsgemmeke.battlegrounds.game;

import com.github.matsgemmeke.battlegrounds.api.game.Session;
import com.github.matsgemmeke.battlegrounds.api.game.SessionConfiguration;
import com.github.matsgemmeke.battlegrounds.configuration.SessionDataConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Factory class for creating {@link Session} instances.
 */
public class SessionFactory {

    @NotNull
    private File dataDirectory;

    public SessionFactory(@NotNull File dataDirectory) {
        this.dataDirectory = dataDirectory;
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

        return new DefaultSession(id, configuration);
    }
}
