package nl.matsgemmeke.battlegrounds.game;

import com.google.inject.Key;
import com.google.inject.OutOfScopeException;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class GameScopeTest {

    private static final GameKey GAME_KEY = GameKey.ofOpenMode();
    private static final GameContextType TYPE = GameContextType.OPEN_MODE;

    @Test
    public void scopeThrowsOutOfScopeExceptionWhenNoGameContextIsEntered() {
        GameContext gameContext = new GameContext(GAME_KEY, TYPE);
        Key<PlayerRegistry> key = mock();
        Provider<PlayerRegistry> provider = mock();

        GameScope scope = new GameScope();
        scope.enter(gameContext);
        scope.exit();
        Provider<PlayerRegistry> scopeProvider = scope.scope(key, provider);

        assertThatThrownBy(scopeProvider::get)
                .isInstanceOf(OutOfScopeException.class)
                .hasMessageMatching("No GameContext in scope for key: Mock for Key, hashCode: [0-9]*");
    }

    @Test
    public void scopeReturnsProviderThatReturnsInstanceFromGameContext() {
        GameContext gameContext = new GameContext(GAME_KEY, TYPE);
        PlayerRegistry playerRegistry = mock(PlayerRegistry.class);
        Key<PlayerRegistry> key = mock();

        Provider<PlayerRegistry> provider = mock();
        when(provider.get()).thenReturn(playerRegistry);

        GameScope scope = new GameScope();
        scope.enter(gameContext);
        Provider<PlayerRegistry> scopeProvider = scope.scope(key, provider);

        assertThat(scopeProvider.get()).isEqualTo(playerRegistry);
    }

    @Test
    public void runInScopeEntersScopeAndExitsAfterRunningAction() {
        GameContext gameContext = new GameContext(GAME_KEY, TYPE);
        Runnable action = mock(Runnable.class);

        GameScope scope = new GameScope();
        scope.runInScope(gameContext, action);

        assertThat(scope.getCurrentGameContext()).isEmpty();

        verify(action).run();
    }

    @Test
    public void runInScopeEntersScopeAndExitsAfterRunnableThrowsException() {
        GameContext gameContext = new GameContext(GAME_KEY, TYPE);
        Runnable action = () -> {
            throw new RuntimeException();
        };

        GameScope scope = new GameScope();

        assertThatThrownBy(() -> scope.runInScope(gameContext, action)).isInstanceOf(RuntimeException.class);
        assertThat(scope.getCurrentGameContext()).isEmpty();
    }

    @Test
    public void supplyInScopeEntersScopeAndExitsAfterReturningValue() {
        GameContext gameContext = new GameContext(GAME_KEY, TYPE);
        Supplier<String> supplier = () -> "test";

        GameScope scope = new GameScope();
        String result = scope.supplyInScope(gameContext, supplier);

        assertThat(result).isEqualTo("test");
        assertThat(scope.getCurrentGameContext()).isEmpty();
    }

    @Test
    public void supplyInScopeEntersScopeAndExitsAfterSupplierThrowsException() {
        GameContext gameContext = new GameContext(GAME_KEY, TYPE);
        Supplier<String> supplier = () -> {
            throw new RuntimeException();
        };

        GameScope scope = new GameScope();

        assertThatThrownBy(() -> scope.supplyInScope(gameContext, supplier)).isInstanceOf(RuntimeException.class);
        assertThat(scope.getCurrentGameContext()).isEmpty();
    }
}
