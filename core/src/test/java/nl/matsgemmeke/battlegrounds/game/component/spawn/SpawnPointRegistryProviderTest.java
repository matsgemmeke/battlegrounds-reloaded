package nl.matsgemmeke.battlegrounds.game.component.spawn;

import com.google.inject.OutOfScopeException;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextType;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.game.openmode.component.spawn.OpenModeSpawnPointRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SpawnPointRegistryProviderTest {

    private GameScope gameScope;
    private Provider<OpenModeSpawnPointRegistry> openModeSpawnPointRegistryProvider;

    @BeforeEach
    public void setUp() {
        gameScope = mock(GameScope.class);
        openModeSpawnPointRegistryProvider = mock();
    }

    @Test
    public void getThrowsOutOfScopeExceptionWhenGameScopeHasNoCurrentGameContext() {
        when(gameScope.getCurrentGameContext()).thenReturn(Optional.empty());

        SpawnPointRegistryProvider provider = new SpawnPointRegistryProvider(gameScope, openModeSpawnPointRegistryProvider);

        assertThatThrownBy(provider::get)
                .isInstanceOf(OutOfScopeException.class)
                .hasMessage("Cannot provide SpawnPointRegistry when the game scope is empty");
    }

    @Test
    public void getReturnsNullWhenCurrentGameContextIsOfTypeArenaMode() {
        GameContext gameContext = new GameContext(GameContextType.ARENA_MODE);

        when(gameScope.getCurrentGameContext()).thenReturn(Optional.of(gameContext));

        SpawnPointRegistryProvider provider = new SpawnPointRegistryProvider(gameScope, openModeSpawnPointRegistryProvider);
        SpawnPointRegistry result = provider.get();

        assertThat(result).isNull();
    }

    @Test
    public void getReturnsOpenModeSpawnPointRegistryWhenCurrentGameContextIsOfTypeOpenMode() {
        GameContext gameContext = new GameContext(GameContextType.OPEN_MODE);
        OpenModeSpawnPointRegistry spawnPointRegistry = mock(OpenModeSpawnPointRegistry.class);

        when(gameScope.getCurrentGameContext()).thenReturn(Optional.of(gameContext));
        when(openModeSpawnPointRegistryProvider.get()).thenReturn(spawnPointRegistry);

        SpawnPointRegistryProvider provider = new SpawnPointRegistryProvider(gameScope, openModeSpawnPointRegistryProvider);
        SpawnPointRegistry result = provider.get();

        assertThat(result).isEqualTo(spawnPointRegistry);
    }
}
