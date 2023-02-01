package com.github.matsgemmeke.battlegrounds.api.game;

/**
 * Holds configurable variables for a {@link GameContext} instance.
 */
public interface GameConfiguration {

    /**
     * Gets the duration of the lobby countdown in seconds.
     *
     * @return the lobby countdown duration
     */
    int getLobbyCountdownDuration();

    /**
     * Gets the maximum amount of players allowed in the game.
     *
     * @return the maximum allowed amount of players
     */
    int getMaxPlayers();

    /**
     * Gets the minimum amount of players allowed in the game.
     *
     * @return the minimum allowed amount of players
     */
    int getMinPlayers();
}

