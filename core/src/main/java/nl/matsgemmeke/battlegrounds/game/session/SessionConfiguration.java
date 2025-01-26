package nl.matsgemmeke.battlegrounds.game.session;

/**
 * Holds configurable variables for a {@link Session} instance.
 */
public class SessionConfiguration {

    private static final int DEFAULT_LOBBY_COUNTDOWN_DURATION = 60;
    private static final int DEFAULT_MIN_PLAYERS = 2;
    private static final int DEFAULT_MAX_PLAYERS = 12;

    private int lobbyCountdownDuration;
    private int maxPlayers;
    private int minPlayers;

    public SessionConfiguration(int lobbyCountdownDuration, int minPlayers, int maxPlayers) {
        this.lobbyCountdownDuration = lobbyCountdownDuration;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
    }

    /**
     * Makes a new configuration with default configuration values.
     *
     * @return a new default configuration
     */
    public static SessionConfiguration getNewConfiguration() {
        return new SessionConfiguration(DEFAULT_LOBBY_COUNTDOWN_DURATION, DEFAULT_MAX_PLAYERS, DEFAULT_MIN_PLAYERS);
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

