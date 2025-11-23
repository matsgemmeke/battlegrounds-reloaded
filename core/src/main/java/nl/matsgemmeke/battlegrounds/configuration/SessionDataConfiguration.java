package nl.matsgemmeke.battlegrounds.configuration;

import nl.matsgemmeke.battlegrounds.game.session.Session;
import nl.matsgemmeke.battlegrounds.game.session.SessionConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;

/**
 * Class used to load and save configurations for a {@link Session} instance in a config file.
 */
public class SessionDataConfiguration extends BasePluginConfiguration {

    public SessionDataConfiguration(@NotNull File file) {
        super(file, (InputStream) null);
    }

    @NotNull
    public SessionConfiguration loadConfiguration() {
        int lobbyCountdownDuration = this.getInteger("config.lobby-countdown-duration");
        int maxPlayers = this.getInteger("config.max-players");
        int minPlayers = this.getInteger("config.min-players");

        return new SessionConfiguration(lobbyCountdownDuration, minPlayers, maxPlayers);
    }

    public void saveConfiguration(@NotNull SessionConfiguration config) {
        this.setValue("config.lobby-countdown-duration", config.getLobbyCountdownDuration());
        this.setValue("config.min-players", config.getMinPlayers());
        this.setValue("config.max-players", config.getMaxPlayers());
        this.save();
    }
}
