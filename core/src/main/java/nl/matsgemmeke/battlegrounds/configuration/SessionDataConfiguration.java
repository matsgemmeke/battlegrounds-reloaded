package nl.matsgemmeke.battlegrounds.configuration;

import nl.matsgemmeke.battlegrounds.game.session.ArenaConfiguration;

import java.io.File;
import java.io.InputStream;

/**
 * Class used to load and save configurations for an arena instance in a config file.
 */
public class SessionDataConfiguration extends BasePluginConfiguration {

    public SessionDataConfiguration(File file) {
        super(file, (InputStream) null);
    }

    public ArenaConfiguration loadConfiguration() {
        int lobbyCountdownDuration = this.getInteger("config.lobby-countdown-duration");
        int maxPlayers = this.getInteger("config.max-players");
        int minPlayers = this.getInteger("config.min-players");

        return new ArenaConfiguration(lobbyCountdownDuration, maxPlayers, minPlayers);
    }

    public void saveConfiguration(ArenaConfiguration config) {
        this.setValue("config.lobby-countdown-duration", config.getLobbyCountdownDuration());
        this.setValue("config.max-players", config.getMaxPlayers());
        this.setValue("config.min-players", config.getMinPlayers());
        this.save();
    }
}
