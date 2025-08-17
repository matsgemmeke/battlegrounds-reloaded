package nl.matsgemmeke.battlegrounds.game.component.state;

import com.google.inject.OutOfScopeException;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextType;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.game.component.storage.StatePersistenceHandler;
import nl.matsgemmeke.battlegrounds.game.component.storage.StatePersistenceHandlerProvider;
import nl.matsgemmeke.battlegrounds.game.openmode.component.storage.OpenModeStatePersistenceHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StatePersistenceHandlerProviderTest {

    private GameScope gameScope;
    private Provider<OpenModeStatePersistenceHandler> openModeStatePersistenceHandlerProvider;

    @BeforeEach
    public void setUp() {
        gameScope = mock(GameScope.class);
        openModeStatePersistenceHandlerProvider = mock();
    }

    @Test
    public void getThrowsOutOfScopeExceptionWhenGameScopeHasNoCurrentGameContext() {
        when(gameScope.getCurrentGameContext()).thenReturn(Optional.empty());

        StatePersistenceHandlerProvider provider = new StatePersistenceHandlerProvider(gameScope, openModeStatePersistenceHandlerProvider);

        assertThatThrownBy(provider::get)
                .isInstanceOf(OutOfScopeException.class)
                .hasMessage("Cannot provide instance of StatePersistenceHandler when the game scope is empty");
    }

    @Test
    public void getReturnsNullWhenCurrentGameContextIsOfTypeArenaMode() {
        GameContext gameContext = new GameContext(GameKey.ofSession(1), GameContextType.ARENA_MODE);

        when(gameScope.getCurrentGameContext()).thenReturn(Optional.of(gameContext));

        StatePersistenceHandlerProvider provider = new StatePersistenceHandlerProvider(gameScope, openModeStatePersistenceHandlerProvider);
        StatePersistenceHandler result = provider.get();

        assertThat(result).isNull();
    }

    @Test
    public void getReturnsOpenModePlayerLifecycleHandlerWhenCurrentGameContextIsOfTypeOpenMode() {
        GameContext gameContext = new GameContext(GameKey.ofOpenMode(), GameContextType.OPEN_MODE);
        OpenModeStatePersistenceHandler statePersistenceHandler = mock(OpenModeStatePersistenceHandler.class);

        when(gameScope.getCurrentGameContext()).thenReturn(Optional.of(gameContext));
        when(openModeStatePersistenceHandlerProvider.get()).thenReturn(statePersistenceHandler);

        StatePersistenceHandlerProvider provider = new StatePersistenceHandlerProvider(gameScope, openModeStatePersistenceHandlerProvider);
        StatePersistenceHandler result = provider.get();

        assertThat(result).isEqualTo(statePersistenceHandler);
    }
}
