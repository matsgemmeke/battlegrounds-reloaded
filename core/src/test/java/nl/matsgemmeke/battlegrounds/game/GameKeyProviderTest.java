package nl.matsgemmeke.battlegrounds.game;

import com.google.inject.OutOfScopeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GameKeyProviderTest {

    private GameScope gameScope;

    @BeforeEach
    public void setUp() {
        gameScope = mock(GameScope.class);
    }

    @Test
    public void getThrowsOutOfScopeExceptionWhenGameScopeHasNoEnteredGameContext() {
        when(gameScope.getCurrentGameContext()).thenReturn(Optional.empty());

        GameKeyProvider provider = new GameKeyProvider(gameScope);

        assertThatThrownBy(provider::get)
                .isInstanceOf(OutOfScopeException.class)
                .hasMessage("No game context active");
    }

    @Test
    public void getReturnsGameKeyOfEnteredGameContext() {
        GameKey gameKey = GameKey.ofOpenMode();
        GameContext gameContext = new GameContext(gameKey, GameContextType.OPEN_MODE);

        when(gameScope.getCurrentGameContext()).thenReturn(Optional.of(gameContext));

        GameKeyProvider provider = new GameKeyProvider(gameScope);
        GameKey result = provider.get();

        assertThat(result).isEqualTo(gameKey);
    }
}
