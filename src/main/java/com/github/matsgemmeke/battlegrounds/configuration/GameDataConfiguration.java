package com.github.matsgemmeke.battlegrounds.configuration;

import com.github.matsgemmeke.battlegrounds.api.game.GameConfiguration;
import com.github.matsgemmeke.battlegrounds.game.DefaultGameConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Class used to load and save configurations for a {@link GameContext} instance in a config file.
 */
public class GameDataConfiguration extends AbstractPluginConfiguration {

    public GameDataConfiguration(@NotNull File file) {
        super(file, null, false);
    }

    @NotNull
    public GameConfiguration loadConfiguration() {
        int lobbyCountdownDuration = this.getInteger("config.lobby-countdown-duration");
        int maxPlayers = this.getInteger("config.max-players");
        int minPlayers = this.getInteger("config.min-players");

        return new DefaultGameConfiguration(lobbyCountdownDuration, maxPlayers, minPlayers);
    }

    public void saveConfiguration(@NotNull GameConfiguration gameConfiguration) {
        this.setValue("config.lobby-countdown-duration", gameConfiguration.getLobbyCountdownDuration());
        this.setValue("config.max-players", gameConfiguration.getMaxPlayers());
        this.setValue("config.min-players", gameConfiguration.getMinPlayers());
        this.save();
    }
}
