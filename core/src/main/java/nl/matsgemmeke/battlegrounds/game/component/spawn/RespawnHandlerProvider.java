package nl.matsgemmeke.battlegrounds.game.component.spawn;

import com.google.inject.Inject;
import com.google.inject.OutOfScopeException;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.game.openmode.component.spawn.OpenModeRespawnHandler;
import org.jetbrains.annotations.NotNull;

public class RespawnHandlerProvider implements Provider<RespawnHandler> {

    @NotNull
    private final GameScope gameScope;
    @NotNull
    private final Provider<OpenModeRespawnHandler> openModeRespawnHandlerProvider;

    @Inject
    public RespawnHandlerProvider(
            @NotNull GameScope gameScope,
            @NotNull Provider<OpenModeRespawnHandler> openModeRespawnHandlerProvider
    ) {
        this.gameScope = gameScope;
        this.openModeRespawnHandlerProvider = openModeRespawnHandlerProvider;
    }

    public RespawnHandler get() {
        GameContext gameContext = gameScope.getCurrentGameContext()
                .orElseThrow(() -> new OutOfScopeException("Cannot provide instance of RespawnHandler when the game scope is empty"));

        return switch (gameContext.getType()) {
            case ARENA_MODE -> null;
            case OPEN_MODE -> openModeRespawnHandlerProvider.get();
        };
    }
}
