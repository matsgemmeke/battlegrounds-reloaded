package nl.matsgemmeke.battlegrounds.game.session;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DefaultSessionConfigurationTest {

    private int lobbyCountdownDuration;
    private int maxPlayers;
    private int minPlayers;

    @BeforeEach
    public void setUp() {
        this.lobbyCountdownDuration = 60;
        this.maxPlayers = 10;
        this.minPlayers = 2;
    }

    @Test
    public void shouldBeAbleToGetDefaultSessionConfigurationInstance() {
        DefaultSessionConfiguration configuration = DefaultSessionConfiguration.getNewConfiguration();

        assertTrue(configuration.getLobbyCountdownDuration() > 0);
        assertTrue(configuration.getMaxPlayers() > 0);
        assertTrue(configuration.getMinPlayers() > 0);
    }

    @Test
    public void shouldBeAbleToGetLobbyCountdownDuration() {
        DefaultSessionConfiguration configuration = new DefaultSessionConfiguration(lobbyCountdownDuration, maxPlayers, minPlayers);

        assertEquals(lobbyCountdownDuration, configuration.getLobbyCountdownDuration());
    }

    @Test
    public void shouldBeAbleToGetMaxPlayers() {
        DefaultSessionConfiguration configuration = new DefaultSessionConfiguration(lobbyCountdownDuration, maxPlayers, minPlayers);

        assertEquals(maxPlayers, configuration.getMaxPlayers());
    }

    @Test
    public void shouldBeAbleToGetMinPlayers() {
        DefaultSessionConfiguration configuration = new DefaultSessionConfiguration(lobbyCountdownDuration, maxPlayers, minPlayers);

        assertEquals(minPlayers, configuration.getMinPlayers());
    }
}
