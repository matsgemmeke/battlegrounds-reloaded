package nl.matsgemmeke.battlegrounds.game.session;

/**
 * Holds configurable variables for a {@link Session} instance.
 */
public interface SessionConfiguration {

    /**
     * Gets the duration of the lobby countdown in seconds.
     *
     * @return the lobby countdown duration
     */
    int getLobbyCountdownDuration();

    /**
     * Gets the maximum amount of players allowed in the session.
     *
     * @return the maximum allowed amount of players
     */
    int getMaxPlayers();

    /**
     * Gets the minimum amount of players allowed in the session.
     *
     * @return the minimum allowed amount of players
     */
    int getMinPlayers();
}

