package nl.matsgemmeke.battlegrounds.game;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GameKeyTest {

    @Test
    @DisplayName("hashCode returns equal code for same game key")
    void hashCode_sameKey() {
        GameKey first = GameKey.ofFreeplay();
        GameKey second = GameKey.ofFreeplay();

        assertThat(first.hashCode()).isEqualTo(second.hashCode());
    }

    @Test
    @DisplayName("hashCode returns different code for different game key")
    void hashcode_differentKey() {
        GameKey first = GameKey.ofFreeplay();
        GameKey second = GameKey.ofArena(1);

        assertThat(first.hashCode()).isNotEqualTo(second.hashCode());
    }

    @Test
    @DisplayName("hashCode can be used as hash map key")
    void hashCode_inHashMap() {
        Map<GameKey, String> map = new HashMap<>();
        map.put(GameKey.ofFreeplay(), "test");

        assertThat(map.get(GameKey.ofFreeplay())).isEqualTo("test");
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
