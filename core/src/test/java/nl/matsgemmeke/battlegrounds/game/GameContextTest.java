package nl.matsgemmeke.battlegrounds.game;

import com.google.inject.Key;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GameContextTest {

    private static final GameKey GAME_KEY = GameKey.ofOpenMode();
    private static final GameContextType TYPE = GameContextType.OPEN_MODE;

    @Test
    public void getScopedObjectReturnsResultFromGivenProviderWhenKeyIsNotStored() {
        PlayerRegistry playerRegistry = mock(PlayerRegistry.class);
        Key<PlayerRegistry> key = mock();

        Provider<PlayerRegistry> provider = mock();
        when(provider.get()).thenReturn(playerRegistry);

        GameContext gameContext = new GameContext(GAME_KEY, TYPE);
        PlayerRegistry result = gameContext.getScopedObject(key, provider);

        assertThat(result).isEqualTo(playerRegistry);
    }

    @Test
    public void getScopedObjectReturnsStoredInstanceWhenKeyIsAlreadyStored() {
        PlayerRegistry firstPlayerRegistry = mock(PlayerRegistry.class);
        PlayerRegistry secondPlayerRegistry = mock(PlayerRegistry.class);
        Key<PlayerRegistry> key = mock();

        Provider<PlayerRegistry> firstProvider = mock();
        when(firstProvider.get()).thenReturn(firstPlayerRegistry);

        Provider<PlayerRegistry> secondProvider = mock();
        when(secondProvider.get()).thenReturn(secondPlayerRegistry);

        GameContext gameContext = new GameContext(GAME_KEY, TYPE);
        gameContext.getScopedObject(key, firstProvider);
        PlayerRegistry result = gameContext.getScopedObject(key, secondProvider);

        assertThat(result).isEqualTo(firstPlayerRegistry);
        assertThat(result).isNotEqualTo(secondPlayerRegistry);
    }
}
