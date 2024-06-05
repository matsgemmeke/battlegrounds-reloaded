package nl.matsgemmeke.battlegrounds.game.session;

/**
 * Holds configurable variables for a session instance.
 */
public class DefaultSessionConfiguration implements SessionConfiguration {

    private int lobbyCountdownDuration;
    private int maxPlayers;
    private int minPlayers;

    public DefaultSessionConfiguration(int lobbyCountdownDuration, int maxPlayers, int minPlayers) {
        this.lobbyCountdownDuration = lobbyCountdownDuration;
        this.maxPlayers = maxPlayers;
        this.minPlayers = minPlayers;
    }

    /**
     * Makes a new configuration with default configuration values.
     *
     * @return a new default configuration
     */
    public static DefaultSessionConfiguration getNewConfiguration() {
        int lobbyCountdownDuration = 60;
        int maxPlayers = 12;
        int minPlayers = 2;

        return new DefaultSessionConfiguration(lobbyCountdownDuration, maxPlayers, minPlayers);
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
