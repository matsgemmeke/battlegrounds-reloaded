package nl.matsgemmeke.battlegrounds.game;

import com.google.inject.Key;
import com.google.inject.OutOfScopeException;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GameScopeTest {

    @Test
    public void scopeThrowsOutOfScopeExceptionWhenNoGameContextIsEntered() {
        GameContext gameContext = new GameContext();
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
        GameContext gameContext = new GameContext();
        PlayerRegistry playerRegistry = mock(PlayerRegistry.class);
        Key<PlayerRegistry> key = mock();

        Provider<PlayerRegistry> provider = mock();
        when(provider.get()).thenReturn(playerRegistry);

        GameScope scope = new GameScope();
        scope.enter(gameContext);
        Provider<PlayerRegistry> scopeProvider = scope.scope(key, provider);

        assertThat(scopeProvider.get()).isEqualTo(playerRegistry);
    }
}
