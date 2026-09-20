package nl.matsgemmeke.battlegrounds.game.component.spawn;

import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import nl.matsgemmeke.battlegrounds.freeplay.FreeplayGameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextType;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.game.component.ComponentProvisionException;
import nl.matsgemmeke.battlegrounds.game.freeplay.component.spawn.FreeplayRespawnHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RespawnHandlerProviderTest {

    private final FreeplayGameContext GAME_CONTEXT = new FreeplayGameContext();

    @Mock
    private GameScope gameScope;
    @Spy
    private Map<GameContextType, Provider<RespawnHandler>> implementations = new HashMap<>();
    @Spy
    private TypeLiteral<RespawnHandler> typeLiteral = TypeLiteral.get(RespawnHandler.class);
    @InjectMocks
    private RespawnHandlerProvider respawnHandlerProvider;

    @Test
    @DisplayName("get throws ComponentProvisionException when game scope has not entered any game context")
    void get_withoutEnteredGameContext() {
        when(gameScope.getCurrentGameContext()).thenReturn(Optional.empty());

        assertThatThrownBy(respawnHandlerProvider::get)
                .isInstanceOf(ComponentProvisionException.class)
                .hasMessage("Cannot provide instance of RespawnHandler: the game scope is empty");
    }

    @Test
    @DisplayName("get throws ComponentProvisionException when provider contains no implementation type for game context type")
    void get_noImplementationTypesFound() {
        implementations.put(GameContextType.ARENA_MODE, mock());

        when(gameScope.getCurrentGameContext()).thenReturn(Optional.of(GAME_CONTEXT));

        assertThatThrownBy(respawnHandlerProvider::get)
                .isInstanceOf(ComponentProvisionException.class)
                .hasMessage("Cannot provide instance of RespawnHandler: no implementation bound for FREEPLAY_MODE");
    }

    @Test
    @DisplayName("get returns instance bound to type of active game context")
    void get_successful() {
        FreeplayRespawnHandler respawnHandler = mock(FreeplayRespawnHandler.class);

        Provider<RespawnHandler> freeplayRespawnHandlerProvider = mock();
        when(freeplayRespawnHandlerProvider.get()).thenReturn(respawnHandler);

        implementations.put(GameContextType.FREEPLAY_MODE, freeplayRespawnHandlerProvider);

        when(gameScope.getCurrentGameContext()).thenReturn(Optional.of(GAME_CONTEXT));

        RespawnHandler result = respawnHandlerProvider.get();

        assertThat(result).isEqualTo(respawnHandler);
    }
}
