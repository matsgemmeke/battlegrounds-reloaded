package nl.matsgemmeke.battlegrounds.game.mapper;

import nl.matsgemmeke.battlegrounds.game.arena.ArenaSettings;
import nl.matsgemmeke.battlegrounds.game.configuration.ArenaSettingsSpec;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ArenaSettingsMapperTest {

    private static final int LOBBY_COUNTDOWN_LENGTH = 60;
    private static final int MAX_PLAYERS = 10;
    private static final int MIN_PLAYERS = 2;

    private final ArenaSettingsMapper mapper = new ArenaSettingsMapper();

    @Test
    @DisplayName("toSpec returns ArenaSettingsSpec with values from given ArenaSettings")
    void toSpec() {
        ArenaSettings settings = new ArenaSettings(LOBBY_COUNTDOWN_LENGTH, MAX_PLAYERS, MIN_PLAYERS);

        ArenaSettingsSpec spec = mapper.toSpec(settings);

        assertThat(spec.lobbyCountdownLength()).isEqualTo(LOBBY_COUNTDOWN_LENGTH);
        assertThat(spec.maxPlayers()).isEqualTo(MAX_PLAYERS);
        assertThat(spec.minPlayers()).isEqualTo(MIN_PLAYERS);
    }
}
