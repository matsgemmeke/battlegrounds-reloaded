package nl.matsgemmeke.battlegrounds.game.component.spawn;

import com.google.inject.Inject;
import com.google.inject.OutOfScopeException;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.game.openmode.component.spawn.OpenModeSpawnPointRegistry;
import org.jetbrains.annotations.NotNull;

public class SpawnPointRegistryProvider implements Provider<SpawnPointRegistry> {

    @NotNull
    private final GameScope gameScope;
    @NotNull
    private final Provider<OpenModeSpawnPointRegistry> openModeSpawnPointRegistryProvider;

    @Inject
    public SpawnPointRegistryProvider(
            @NotNull GameScope gameScope,
            @NotNull Provider<OpenModeSpawnPointRegistry> openModeSpawnPointRegistryProvider
    ) {
        this.gameScope = gameScope;
        this.openModeSpawnPointRegistryProvider = openModeSpawnPointRegistryProvider;
    }

    public SpawnPointRegistry get() {
        GameContext gameContext = gameScope.getCurrentGameContext()
                .orElseThrow(() -> new OutOfScopeException("Cannot provide instance of SpawnPointRegistry when the game scope is empty"));

        return switch (gameContext.getType()) {
            case ARENA_MODE -> null;
            case OPEN_MODE -> openModeSpawnPointRegistryProvider.get();
        };
    }
}
