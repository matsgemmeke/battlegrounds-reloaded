package nl.matsgemmeke.battlegrounds.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameKeyTest {

    @Test
    public void toStringReturnsCorrectValueForSessionKeys() {
        GameKey gameKey = GameKey.ofSession(1);
        String value = gameKey.toString();

        assertEquals("SESSION-1", value);
    }

    @Test
    public void toStringReturnsCorrectValueForOpenModeKeys() {
        GameKey gameKey = GameKey.ofOpenMode();
        String value = gameKey.toString();

        assertEquals("OPEN-MODE", value);
    }
}
