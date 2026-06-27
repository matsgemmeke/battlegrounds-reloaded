package nl.matsgemmeke.battlegrounds.game.arena;

import nl.matsgemmeke.battlegrounds.game.BaseGame;
import nl.matsgemmeke.battlegrounds.game.arena.settings.ArenaSettings;

/**
 * Represents an arena which groups multiple players to play various kinds of game modes.
 */
public class Arena extends BaseGame {

    private final int id;
    private final ArenaSettings settings;

    public Arena(int id, ArenaSettings settings) {
        this.id = id;
        this.settings = settings;
    }

    public int getId() {
        return id;
    }

    public ArenaSettings getSettings() {
        return settings;
    }
}
