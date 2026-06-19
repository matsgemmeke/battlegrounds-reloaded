package nl.matsgemmeke.battlegrounds.game.component.spawn;

import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextType;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.game.component.ComponentProvisionException;
import nl.matsgemmeke.battlegrounds.game.openmode.component.spawn.FreeplayRespawnHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RespawnHandlerProviderTest {

    private GameScope gameScope;
    private TypeLiteral<RespawnHandler> typeLiteral;

    @BeforeEach
    void setUp() {
        gameScope = mock(GameScope.class);
        typeLiteral = TypeLiteral.get(RespawnHandler.class);
    }

    @Test
    @DisplayName("get throws ComponentProvisionException when game scope has not entered any game context")
    void get_withoutEnteredGameContext() {
        Map<GameContextType, Provider<RespawnHandler>> implementations = Map.of();

        when(gameScope.getCurrentGameContext()).thenReturn(Optional.empty());

        RespawnHandlerProvider provider = new RespawnHandlerProvider(gameScope, implementations, typeLiteral);

        assertThatThrownBy(provider::get)
                .isInstanceOf(ComponentProvisionException.class)
                .hasMessage("Cannot provide instance of RespawnHandler: the game scope is empty");
    }

    @Test
    @DisplayName("get throws ComponentProvisionException when provider contains no implementation type for game context type")
    void get_noImplementationTypesFound() {
        GameContext gameContext = new GameContext(GameKey.ofFreeplay(), GameContextType.ARENA_MODE);
        Map<GameContextType, Provider<RespawnHandler>> implementations = Map.of(GameContextType.OPEN_MODE, mock());

        when(gameScope.getCurrentGameContext()).thenReturn(Optional.of(gameContext));

        RespawnHandlerProvider provider = new RespawnHandlerProvider(gameScope, implementations, typeLiteral);

        assertThatThrownBy(provider::get)
                .isInstanceOf(ComponentProvisionException.class)
                .hasMessage("Cannot provide instance of RespawnHandler: no implementation bound for ARENA_MODE");
    }

    @Test
    @DisplayName("get returns instance bound to type of active game context")
    void get_successful() {
        GameContext gameContext = new GameContext(GameKey.ofFreeplay(), GameContextType.OPEN_MODE);
        FreeplayRespawnHandler respawnHandler = mock(FreeplayRespawnHandler.class);

        Provider<RespawnHandler> freeplayRespawnHandlerProvider = mock();
        when(freeplayRespawnHandlerProvider.get()).thenReturn(respawnHandler);

        Map<GameContextType, Provider<RespawnHandler>> implementations = Map.of(GameContextType.OPEN_MODE, freeplayRespawnHandlerProvider);

        when(gameScope.getCurrentGameContext()).thenReturn(Optional.of(gameContext));

        RespawnHandlerProvider provider = new RespawnHandlerProvider(gameScope, implementations, typeLiteral);
        RespawnHandler result = provider.get();

        assertThat(result).isEqualTo(respawnHandler);
    }
}
