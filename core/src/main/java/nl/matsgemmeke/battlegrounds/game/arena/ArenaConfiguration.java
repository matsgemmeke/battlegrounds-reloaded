package nl.matsgemmeke.battlegrounds.game.arena;

/**
 * Holds configurable variables for an {@link Arena} instance.
 */
public class ArenaConfiguration {

    private static final int DEFAULT_LOBBY_COUNTDOWN_DURATION = 60;
    private static final int DEFAULT_MIN_PLAYERS = 2;
    private static final int DEFAULT_MAX_PLAYERS = 12;

    private int lobbyCountdownDuration;
    private int maxPlayers;
    private int minPlayers;

    public ArenaConfiguration(int lobbyCountdownDuration, int maxPlayers, int minPlayers) {
        this.lobbyCountdownDuration = lobbyCountdownDuration;
        this.maxPlayers = maxPlayers;
        this.minPlayers = minPlayers;
    }

    /**
     * Makes a new configuration with default configuration values.
     *
     * @return a new default configuration
     */
    public static ArenaConfiguration getNewConfiguration() {
        return new ArenaConfiguration(DEFAULT_LOBBY_COUNTDOWN_DURATION, DEFAULT_MAX_PLAYERS, DEFAULT_MIN_PLAYERS);
    }

    public int getLobbyCountdownDuration() {
        return lobbyCountdownDuration;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getMinPlayers() {
        return minPlayers;
    }
}

