package nl.matsgemmeke.battlegrounds.game;

import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.game.component.storage.StatePersistenceHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.mockito.Mockito.*;

public class GameContextShutdownManagerTest {

    private static final GameKey OPEN_MODE_GAME_KEY = GameKey.ofOpenMode();

    private GameContextProvider gameContextProvider;
    private GameScope gameScope;
    private Provider<StatePersistenceHandler> statePersistenceHandlerProvider;

    @BeforeEach
    public void setUp() {
        gameContextProvider = mock(GameContextProvider.class);
        gameScope = mock(GameScope.class);
        statePersistenceHandlerProvider = mock();
    }

    @Test
    public void shutdownDoesNothingWhenAnOpenModeGameContextDoesNotExists() {
        when(gameContextProvider.getGameContext(OPEN_MODE_GAME_KEY)).thenReturn(Optional.empty());

        GameContextShutdownManager gameContextShutdownManager = new GameContextShutdownManager(gameContextProvider, gameScope, statePersistenceHandlerProvider);
        gameContextShutdownManager.shutdown();

        verifyNoInteractions(gameScope);
    }

    @Test
    public void shutdownSavesOpenModeState() {
        GameContext gameContext = new GameContext(OPEN_MODE_GAME_KEY, GameContextType.OPEN_MODE);
        StatePersistenceHandler statePersistenceHandler = mock(StatePersistenceHandler.class);

        when(gameContextProvider.getGameContext(OPEN_MODE_GAME_KEY)).thenReturn(Optional.of(gameContext));
        when(statePersistenceHandlerProvider.get()).thenReturn(statePersistenceHandler);

        GameContextShutdownManager gameContextShutdownManager = new GameContextShutdownManager(gameContextProvider, gameScope, statePersistenceHandlerProvider);
        gameContextShutdownManager.shutdown();

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(gameScope).runInScope(eq(gameContext), runnableCaptor.capture());

        runnableCaptor.getValue().run();

        verify(statePersistenceHandler).saveState();
    }
}
