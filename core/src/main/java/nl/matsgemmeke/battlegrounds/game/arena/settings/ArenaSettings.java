package nl.matsgemmeke.battlegrounds.game.arena.settings;

import nl.matsgemmeke.battlegrounds.game.arena.Arena;

/**
 * Contains settings variables for an {@link Arena} instance.
 */
public class ArenaSettings {

    private static final int DEFAULT_LOBBY_COUNTDOWN_DURATION = 60;
    private static final int DEFAULT_MIN_PLAYERS = 2;
    private static final int DEFAULT_MAX_PLAYERS = 12;

    private final int lobbyCountdownLength;
    private final int maxPlayers;
    private final int minPlayers;

    public ArenaSettings(int lobbyCountdownLength, int maxPlayers, int minPlayers) {
        this.lobbyCountdownLength = lobbyCountdownLength;
        this.maxPlayers = maxPlayers;
        this.minPlayers = minPlayers;
    }

    /**
     * Creates a new settings instance with default configuration values.
     *
     * @return a new default settings object
     */
    public static ArenaSettings getDefaultSettings() {
        return new ArenaSettings(DEFAULT_LOBBY_COUNTDOWN_DURATION, DEFAULT_MAX_PLAYERS, DEFAULT_MIN_PLAYERS);
    }

    public int getLobbyCountdownLength() {
        return lobbyCountdownLength;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getMinPlayers() {
        return minPlayers;
    }
}

