package nl.matsgemmeke.battlegrounds.game.arena;

import nl.matsgemmeke.battlegrounds.game.BaseGame;

/**
 * Represents an arena which groups multiple players to play various kinds of game modes.
 */
public class Arena extends BaseGame {

    private final ArenaSettings settings;

    public Arena(ArenaSettings settings) {
        this.settings = settings;
    }

    public ArenaSettings getSettings() {
        return settings;
    }
}
