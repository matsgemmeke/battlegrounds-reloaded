package nl.matsgemmeke.battlegrounds.game.session;

import nl.matsgemmeke.battlegrounds.game.BaseGame;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a session which groups multiple players to play various kinds of game modes.
 */
public class Session extends BaseGame {

    private final int id;
    @NotNull
    private SessionConfiguration configuration;

    public Session(int id, @NotNull SessionConfiguration configuration) {
        this.id = id;
        this.configuration = configuration;
    }

    @NotNull
    public SessionConfiguration getConfiguration() {
        return configuration;
    }

    public int getId() {
        return id;
    }
}
