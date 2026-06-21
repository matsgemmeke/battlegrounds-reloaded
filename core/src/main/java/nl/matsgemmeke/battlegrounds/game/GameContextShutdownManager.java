package nl.matsgemmeke.battlegrounds.game;

import com.google.inject.Inject;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.game.component.storage.StatePersistenceHandler;

public class GameContextShutdownManager {

    private static final GameKey FREEPLAY_GAME_KEY = GameKey.ofFreeplay();

    private final GameContextProvider gameContextProvider;
    private final GameScope gameScope;
    private final Provider<StatePersistenceHandler> statePersistenceHandlerProvider;

    @Inject
    public GameContextShutdownManager(GameContextProvider gameContextProvider, GameScope gameScope, Provider<StatePersistenceHandler> statePersistenceHandlerProvider) {
        this.gameContextProvider = gameContextProvider;
        this.gameScope = gameScope;
        this.statePersistenceHandlerProvider = statePersistenceHandlerProvider;
    }

    public void shutdown() {
        GameContext freeplayGameContext = gameContextProvider.getGameContext(FREEPLAY_GAME_KEY).orElse(null);

        if (freeplayGameContext == null) {
            return;
        }

        gameScope.runInScope(freeplayGameContext, () -> {
            StatePersistenceHandler statePersistenceHandler = statePersistenceHandlerProvider.get();
            statePersistenceHandler.saveState();
        });
    }
}
