package com.github.matsgemmeke.battlegrounds.game;

import com.github.matsgemmeke.battlegrounds.api.game.GameConfiguration;

/**
 * Holds configurable variables for a game instance.
 */
public class DefaultGameConfiguration implements GameConfiguration {

    private int lobbyCountdownDuration;
    private int maxPlayers;
    private int minPlayers;

    public DefaultGameConfiguration(int lobbyCountdownDuration, int maxPlayers, int minPlayers) {
        this.lobbyCountdownDuration = lobbyCountdownDuration;
        this.maxPlayers = maxPlayers;
        this.minPlayers = minPlayers;
    }

    /**
     * Makes a new configuration with default configuration values.
     *
     * @return a new default configuration
     */
    public static DefaultGameConfiguration getNewConfiguration() {
        int lobbyCountdownDuration = 60;
        int maxPlayers = 12;
        int minPlayers = 2;

        return new DefaultGameConfiguration(lobbyCountdownDuration, maxPlayers, minPlayers);
    }

    /**
     * Gets the duration of the lobby countdown in seconds.
     *
     * @return the lobby countdown duration
     */
    public int getLobbyCountdownDuration() {
        return lobbyCountdownDuration;
    }

    /**
     * Gets the maximum amount of players allowed in the game.
     *
     * @return the maximum allowed amount of players
     */
    public int getMaxPlayers() {
        return maxPlayers;
    }

    /**
     * Gets the minimum amount of players allowed in the game.
     *
     * @return the minimum allowed amount of players
     */
    public int getMinPlayers() {
        return minPlayers;
    }
}
