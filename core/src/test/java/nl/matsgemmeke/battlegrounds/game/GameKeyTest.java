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
    @ValueSource(strings = { "OPEN-MODE", "ARENA-1" })
    @DisplayName("parse returns game key of open mode when given value equals OPEN-MODE")
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
    @DisplayName("toString returns value for open mode key")
    void toString_openMode() {
        GameKey gameKey = GameKey.ofOpenMode();
        String value = gameKey.toString();

        assertThat(value).isEqualTo("OPEN-MODE");
    }
}
