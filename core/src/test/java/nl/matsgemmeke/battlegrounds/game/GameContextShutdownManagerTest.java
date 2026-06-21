package nl.matsgemmeke.battlegrounds.game;

import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.game.component.storage.StatePersistenceHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameContextShutdownManagerTest {

    private static final GameKey FREEPLAY_GAME_KEY = GameKey.ofFreeplay();

    @Mock
    private GameContextProvider gameContextProvider;
    @Mock
    private GameScope gameScope;
    @Mock
    private Provider<StatePersistenceHandler> statePersistenceHandlerProvider;
    @InjectMocks
    private GameContextShutdownManager gameContextShutdownManager;

    @Test
    @DisplayName("shutdown does nothing when the freeplay game context does not exist")
    void shutdown_withoutFreeplay() {
        when(gameContextProvider.getGameContext(FREEPLAY_GAME_KEY)).thenReturn(Optional.empty());

        GameContextShutdownManager gameContextShutdownManager = new GameContextShutdownManager(gameContextProvider, gameScope, statePersistenceHandlerProvider);
        gameContextShutdownManager.shutdown();

        verifyNoInteractions(gameScope);
    }

    @Test
    @DisplayName("shutdown saves freeplay mode state")
    void shutdown_savesFreeplay() {
        GameContext gameContext = new GameContext(FREEPLAY_GAME_KEY, GameContextType.FREEPLAY_MODE);
        StatePersistenceHandler statePersistenceHandler = mock(StatePersistenceHandler.class);

        when(gameContextProvider.getGameContext(FREEPLAY_GAME_KEY)).thenReturn(Optional.of(gameContext));
        when(statePersistenceHandlerProvider.get()).thenReturn(statePersistenceHandler);

        GameContextShutdownManager gameContextShutdownManager = new GameContextShutdownManager(gameContextProvider, gameScope, statePersistenceHandlerProvider);
        gameContextShutdownManager.shutdown();

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(gameScope).runInScope(eq(gameContext), runnableCaptor.capture());

        runnableCaptor.getValue().run();

        verify(statePersistenceHandler).saveState();
    }
}
