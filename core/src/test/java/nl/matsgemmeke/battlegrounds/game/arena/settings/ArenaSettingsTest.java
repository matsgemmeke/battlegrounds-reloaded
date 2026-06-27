package nl.matsgemmeke.battlegrounds.game.arena.settings;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ArenaSettingsTest {

    private static final int LOBBY_COUNTDOWN_LENGTH = 60;
    private static final int MAX_PLAYERS = 10;
    private static final int MIN_PLAYERS = 2;

    @Test
    @DisplayName("getDefaultSettings returns instance with default values")
    void getDefaultSettings() {
        ArenaSettings settings = ArenaSettings.getDefaultSettings();

        assertThat(settings.getLobbyCountdownLength()).isEqualTo(60);
        assertThat(settings.getMaxPlayers()).isEqualTo(12);
        assertThat(settings.getMinPlayers()).isEqualTo(2);
    }

    @Test
    void getLobbyCountdownLength() {
        ArenaSettings settings = new ArenaSettings(LOBBY_COUNTDOWN_LENGTH, MAX_PLAYERS, MIN_PLAYERS);

        assertThat(settings.getLobbyCountdownLength()).isEqualTo(LOBBY_COUNTDOWN_LENGTH);
    }

    @Test
    void getMaxPlayers() {
        ArenaSettings settings = new ArenaSettings(LOBBY_COUNTDOWN_LENGTH, MAX_PLAYERS, MIN_PLAYERS);

        assertThat(settings.getMaxPlayers()).isEqualTo(MAX_PLAYERS);
    }

    @Test
    void getMinPlayers() {
        ArenaSettings settings = new ArenaSettings(LOBBY_COUNTDOWN_LENGTH, MAX_PLAYERS, MIN_PLAYERS);

        assertThat(settings.getMinPlayers()).isEqualTo(MIN_PLAYERS);
    }
}
