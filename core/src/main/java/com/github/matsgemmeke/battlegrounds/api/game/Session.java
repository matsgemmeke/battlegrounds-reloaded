package com.github.matsgemmeke.battlegrounds.api.game;

/**
 * Represents a session which groups multiple players to play various kinds of game modes.
 */
public interface Session extends BattleContext {

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
