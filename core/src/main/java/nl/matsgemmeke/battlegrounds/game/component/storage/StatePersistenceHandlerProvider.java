package nl.matsgemmeke.battlegrounds.game.component.storage;

import com.google.inject.Inject;
import com.google.inject.OutOfScopeException;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.game.openmode.component.storage.OpenModeStatePersistenceHandler;
import org.jetbrains.annotations.NotNull;

public class StatePersistenceHandlerProvider implements Provider<StatePersistenceHandler> {

    @NotNull
    private final GameScope gameScope;
    @NotNull
    private final Provider<OpenModeStatePersistenceHandler> openModeStatePersistenceHandlerProvider;

    @Inject
    public StatePersistenceHandlerProvider(
            @NotNull GameScope gameScope,
            @NotNull Provider<OpenModeStatePersistenceHandler> openModeStatePersistenceHandlerProvider
    ) {
        this.gameScope = gameScope;
        this.openModeStatePersistenceHandlerProvider = openModeStatePersistenceHandlerProvider;
    }

    public StatePersistenceHandler get() {
        GameContext gameContext = gameScope.getCurrentGameContext()
                .orElseThrow(() -> new OutOfScopeException("Cannot provide instance of StatePersistenceHandler when the game scope is empty"));

        return switch (gameContext.getType()) {
            case ARENA_MODE -> null;
            case OPEN_MODE -> openModeStatePersistenceHandlerProvider.get();
        };
    }
}
