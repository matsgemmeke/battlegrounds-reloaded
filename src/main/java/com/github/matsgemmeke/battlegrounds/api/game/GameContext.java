package com.github.matsgemmeke.battlegrounds.api.game;

/**
 * Represents a lobby which contains a group of players playing a certain kind of game mode.
 */
public interface GameContext extends BattleContext {

    /**
     * Gets the configuration for the game.
     *
     * @return the game configuration
     */
    GameConfiguration getConfiguration();

    /**
     * Gets the identifier of the game.
     *
     * @return the game identifier
     */
    int getId();
}
