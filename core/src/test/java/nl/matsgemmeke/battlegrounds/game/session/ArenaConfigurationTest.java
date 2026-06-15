package nl.matsgemmeke.battlegrounds.game.session;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ArenaConfigurationTest {

    private int lobbyCountdownDuration;
    private int maxPlayers;
    private int minPlayers;

    @BeforeEach
    void setUp() {
        this.lobbyCountdownDuration = 60;
        this.maxPlayers = 10;
        this.minPlayers = 2;
    }

    @Test
    @DisplayName("getNewConfiguration returns default arena configuration instance")
    void getNewConfiguration() {
        ArenaConfiguration configuration = ArenaConfiguration.getNewConfiguration();

        assertThat(configuration.getLobbyCountdownDuration()).isEqualTo(60);
        assertThat(configuration.getMaxPlayers()).isEqualTo(12);
        assertThat(configuration.getMinPlayers()).isEqualTo(2);
    }

    @Test
    void getLobbyCountdownDuration() {
        ArenaConfiguration configuration = new ArenaConfiguration(lobbyCountdownDuration, maxPlayers, minPlayers);

        assertThat(configuration.getLobbyCountdownDuration()).isEqualTo(lobbyCountdownDuration);
    }

    @Test
    void getMaxPlayers() {
        ArenaConfiguration configuration = new ArenaConfiguration(lobbyCountdownDuration, maxPlayers, minPlayers);

        assertThat(configuration.getMaxPlayers()).isEqualTo(maxPlayers);
    }

    @Test
    void getMinPlayers() {
        ArenaConfiguration configuration = new ArenaConfiguration(lobbyCountdownDuration, maxPlayers, minPlayers);

        assertThat(configuration.getMinPlayers()).isEqualTo(minPlayers);
    }
}
