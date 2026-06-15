package nl.matsgemmeke.battlegrounds.game.arena;

import nl.matsgemmeke.battlegrounds.game.BaseGame;

/**
 * Represents an arena which groups multiple players to play various kinds of game modes.
 */
public class Arena extends BaseGame {

    private final ArenaConfiguration configuration;

    public Arena(ArenaConfiguration configuration) {
        this.configuration = configuration;
    }

    public ArenaConfiguration getConfiguration() {
        return configuration;
    }
}
