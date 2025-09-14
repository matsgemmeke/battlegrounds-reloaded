package nl.matsgemmeke.battlegrounds.game.component.spawn;

import com.google.inject.OutOfScopeException;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextType;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.game.openmode.component.spawn.OpenModeRespawnHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RespawnHandlerProviderTest {

    private GameScope gameScope;
    private Provider<OpenModeRespawnHandler> openModeRespawnHandlerProvider;

    @BeforeEach
    public void setUp() {
        gameScope = mock(GameScope.class);
        openModeRespawnHandlerProvider = mock();
    }

    @Test
    public void getThrowsOutOfScopeExceptionWhenGameScopeHasNoCurrentGameContext() {
        when(gameScope.getCurrentGameContext()).thenReturn(Optional.empty());

        RespawnHandlerProvider provider = new RespawnHandlerProvider(gameScope, openModeRespawnHandlerProvider);

        assertThatThrownBy(provider::get)
                .isInstanceOf(OutOfScopeException.class)
                .hasMessage("Cannot provide instance of RespawnHandler when the game scope is empty");
    }

    @Test
    public void getReturnsNullWhenCurrentGameContextIsOfTypeArenaMode() {
        GameContext gameContext = new GameContext(GameKey.ofSession(1), GameContextType.ARENA_MODE);

        when(gameScope.getCurrentGameContext()).thenReturn(Optional.of(gameContext));

        RespawnHandlerProvider provider = new RespawnHandlerProvider(gameScope, openModeRespawnHandlerProvider);
        RespawnHandler result = provider.get();

        assertThat(result).isNull();
    }

    @Test
    public void getReturnsOpenModeSpawnPointRegistryWhenCurrentGameContextIsOfTypeOpenMode() {
        GameContext gameContext = new GameContext(GameKey.ofOpenMode(), GameContextType.OPEN_MODE);
        OpenModeRespawnHandler respawnHandler = mock(OpenModeRespawnHandler.class);

        when(gameScope.getCurrentGameContext()).thenReturn(Optional.of(gameContext));
        when(openModeRespawnHandlerProvider.get()).thenReturn(respawnHandler);

        RespawnHandlerProvider provider = new RespawnHandlerProvider(gameScope, openModeRespawnHandlerProvider);
        RespawnHandler result = provider.get();

        assertThat(result).isEqualTo(respawnHandler);
    }
}
