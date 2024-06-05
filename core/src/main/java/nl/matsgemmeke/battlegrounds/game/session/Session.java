package nl.matsgemmeke.battlegrounds.game.session;

import nl.matsgemmeke.battlegrounds.game.Game;

/**
 * Represents a session which groups multiple players to play various kinds of game modes.
 */
public interface Session extends Game {

    /**
     * Gets the configuration for the session.
     *
     * @return the session configuration
     */
    SessionConfiguration getConfiguration();

    /**
     * Gets the identifier of the session.
     *
     * @return the session identifier
     */
    int getId();
}
