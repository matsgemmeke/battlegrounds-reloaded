package nl.matsgemmeke.battlegrounds.game;

import com.google.inject.Inject;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.game.component.storage.StatePersistenceHandler;
import org.jetbrains.annotations.NotNull;

public class GameContextShutdownManager {

    private static final GameKey OPEN_MODE_GAME_KEY = GameKey.ofOpenMode();

    @NotNull
    private final GameContextProvider gameContextProvider;
    @NotNull
    private final GameScope gameScope;
    @NotNull
    private final Provider<StatePersistenceHandler> statePersistenceHandlerProvider;

    @Inject
    public GameContextShutdownManager(@NotNull GameContextProvider gameContextProvider, @NotNull GameScope gameScope, @NotNull Provider<StatePersistenceHandler> statePersistenceHandlerProvider) {
        this.gameContextProvider = gameContextProvider;
        this.gameScope = gameScope;
        this.statePersistenceHandlerProvider = statePersistenceHandlerProvider;
    }

    public void shutdown() {
        GameContext openModeGameContext = gameContextProvider.getGameContext(OPEN_MODE_GAME_KEY).orElse(null);

        if (openModeGameContext == null) {
            return;
        }

        gameScope.runInScope(openModeGameContext, () -> {
            StatePersistenceHandler statePersistenceHandler = statePersistenceHandlerProvider.get();
            statePersistenceHandler.saveState();
        });
    }
}
