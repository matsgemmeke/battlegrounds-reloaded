package nl.matsgemmeke.battlegrounds.game.component.player;

import com.google.inject.Inject;
import com.google.inject.OutOfScopeException;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.game.openmode.component.player.OpenModePlayerLifecycleHandler;
import org.jetbrains.annotations.NotNull;

public class PlayerLifecycleHandlerProvider implements Provider<PlayerLifecycleHandler> {

    @NotNull
    private final GameScope gameScope;
    @NotNull
    private final Provider<OpenModePlayerLifecycleHandler> openModePlayerLifecycleHandlerProvider;

    @Inject
    public PlayerLifecycleHandlerProvider(
            @NotNull GameScope gameScope,
            @NotNull Provider<OpenModePlayerLifecycleHandler> openModePlayerLifecycleHandlerProvider
    ) {
        this.gameScope = gameScope;
        this.openModePlayerLifecycleHandlerProvider = openModePlayerLifecycleHandlerProvider;
    }

    public PlayerLifecycleHandler get() {
        GameContext gameContext = gameScope.getCurrentGameContext()
                .orElseThrow(() -> new OutOfScopeException("Cannot provide instance of PlayerLifecycleHandler when the game scope is empty"));

        return switch (gameContext.getType()) {
            case ARENA_MODE -> null;
            case OPEN_MODE -> openModePlayerLifecycleHandlerProvider.get();
        };
    }
}
