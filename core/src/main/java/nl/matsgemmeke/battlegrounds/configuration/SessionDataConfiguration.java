package nl.matsgemmeke.battlegrounds.configuration;

import nl.matsgemmeke.battlegrounds.game.session.DefaultSessionConfiguration;
import nl.matsgemmeke.battlegrounds.game.session.Session;
import nl.matsgemmeke.battlegrounds.game.session.SessionConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Class used to load and save configurations for a {@link Session} instance in a config file.
 */
public class SessionDataConfiguration extends BasePluginConfiguration {

    public SessionDataConfiguration(@NotNull File file) {
        super(file, null);
    }

    @NotNull
    public SessionConfiguration loadConfiguration() {
        int lobbyCountdownDuration = this.getInteger("config.lobby-countdown-duration");
        int maxPlayers = this.getInteger("config.max-players");
        int minPlayers = this.getInteger("config.min-players");

        return new DefaultSessionConfiguration(lobbyCountdownDuration, maxPlayers, minPlayers);
    }

    public void saveConfiguration(@NotNull SessionConfiguration config) {
        this.setValue("config.lobby-countdown-duration", config.getLobbyCountdownDuration());
        this.setValue("config.max-players", config.getMaxPlayers());
        this.setValue("config.min-players", config.getMinPlayers());
        this.save();
    }
}
