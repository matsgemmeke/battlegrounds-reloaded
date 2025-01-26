package nl.matsgemmeke.battlegrounds.game.session;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SessionConfigurationTest {

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
        SessionConfiguration configuration = SessionConfiguration.getNewConfiguration();

        assertTrue(configuration.getLobbyCountdownDuration() > 0);
        assertTrue(configuration.getMaxPlayers() > 0);
        assertTrue(configuration.getMinPlayers() > 0);
    }

    @Test
    public void shouldBeAbleToGetLobbyCountdownDuration() {
        SessionConfiguration configuration = new SessionConfiguration(lobbyCountdownDuration, minPlayers, maxPlayers);

        assertEquals(lobbyCountdownDuration, configuration.getLobbyCountdownDuration());
    }

    @Test
    public void shouldBeAbleToGetMaxPlayers() {
        SessionConfiguration configuration = new SessionConfiguration(lobbyCountdownDuration, minPlayers, maxPlayers);

        assertEquals(maxPlayers, configuration.getMaxPlayers());
    }

    @Test
    public void shouldBeAbleToGetMinPlayers() {
        SessionConfiguration configuration = new SessionConfiguration(lobbyCountdownDuration, minPlayers, maxPlayers);

        assertEquals(minPlayers, configuration.getMinPlayers());
    }
}
