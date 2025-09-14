package nl.matsgemmeke.battlegrounds.game.component.player;

import com.google.inject.OutOfScopeException;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextType;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.game.openmode.component.player.OpenModePlayerLifecycleHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PlayerLifecycleHandlerProviderTest {

    private GameScope gameScope;
    private Provider<OpenModePlayerLifecycleHandler> openModePlayerLifecycleHandlerProvider;

    @BeforeEach
    public void setUp() {
        gameScope = mock(GameScope.class);
        openModePlayerLifecycleHandlerProvider = mock();
    }

    @Test
    public void getThrowsOutOfScopeExceptionWhenGameScopeHasNoCurrentGameContext() {
        when(gameScope.getCurrentGameContext()).thenReturn(Optional.empty());

        PlayerLifecycleHandlerProvider provider = new PlayerLifecycleHandlerProvider(gameScope, openModePlayerLifecycleHandlerProvider);

        assertThatThrownBy(provider::get)
                .isInstanceOf(OutOfScopeException.class)
                .hasMessage("Cannot provide instance of PlayerLifecycleHandler when the game scope is empty");
    }

    @Test
    public void getReturnsNullWhenCurrentGameContextIsOfTypeArenaMode() {
        GameContext gameContext = new GameContext(GameKey.ofSession(1), GameContextType.ARENA_MODE);

        when(gameScope.getCurrentGameContext()).thenReturn(Optional.of(gameContext));

        PlayerLifecycleHandlerProvider provider = new PlayerLifecycleHandlerProvider(gameScope, openModePlayerLifecycleHandlerProvider);
        PlayerLifecycleHandler result = provider.get();

        assertThat(result).isNull();
    }

    @Test
    public void getReturnsOpenModePlayerLifecycleHandlerWhenCurrentGameContextIsOfTypeOpenMode() {
        GameContext gameContext = new GameContext(GameKey.ofOpenMode(), GameContextType.OPEN_MODE);
        OpenModePlayerLifecycleHandler playerLifecycleHandler = mock(OpenModePlayerLifecycleHandler.class);

        when(gameScope.getCurrentGameContext()).thenReturn(Optional.of(gameContext));
        when(openModePlayerLifecycleHandlerProvider.get()).thenReturn(playerLifecycleHandler);

        PlayerLifecycleHandlerProvider provider = new PlayerLifecycleHandlerProvider(gameScope, openModePlayerLifecycleHandlerProvider);
        PlayerLifecycleHandler result = provider.get();

        assertThat(result).isEqualTo(playerLifecycleHandler);
    }
}
