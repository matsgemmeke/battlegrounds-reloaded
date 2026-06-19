package nl.matsgemmeke.battlegrounds.game;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GameKeyTest {

    @ParameterizedTest
    @ValueSource(strings = { "FREEPLAY", "ARENA-1" })
    @DisplayName("parse returns game key instances when value equals either FREEPLAY or ARENA-[id]")
    void parse_successful(String value) {
        GameKey gameKey = GameKey.parse(value);

        assertThat(gameKey).isNotNull();
    }

    @ParameterizedTest
    @CsvSource({
            "invalid,Unknown GameKey format: invalid",
            "ARENA-invalid,Invalid ARENA id: invalid"
    })
    void parse_invalidValue(String value, String expectedExceptionMessage) {
        assertThatThrownBy(() -> GameKey.parse(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(expectedExceptionMessage);
    }

    @Test
    @DisplayName("toString returns value for arena keys")
    void toString_arena() {
        GameKey gameKey = GameKey.ofArena(1);
        String value = gameKey.toString();

        assertThat(value).isEqualTo("ARENA-1");
    }

    @Test
    @DisplayName("toString returns value for freeplay key")
    void toString_freeplay() {
        GameKey gameKey = GameKey.ofFreeplay();
        String value = gameKey.toString();

        assertThat(value).isEqualTo("FREEPLAY");
    }
}
