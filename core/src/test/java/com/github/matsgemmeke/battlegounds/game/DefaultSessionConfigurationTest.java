package com.github.matsgemmeke.battlegounds.game;

import com.github.matsgemmeke.battlegrounds.game.DefaultSessionConfiguration;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DefaultSessionConfigurationTest {

    private int lobbyCountdownDuration;
    private int maxPlayers;
    private int minPlayers;

    @Before
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
