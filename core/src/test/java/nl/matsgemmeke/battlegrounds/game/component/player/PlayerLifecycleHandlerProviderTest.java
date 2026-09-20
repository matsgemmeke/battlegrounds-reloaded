package nl.matsgemmeke.battlegrounds.game.component.player;

import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import nl.matsgemmeke.battlegrounds.freeplay.FreeplayGameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextType;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.game.component.ComponentProvisionException;
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
class PlayerLifecycleHandlerProviderTest {

    private final FreeplayGameContext GAME_CONTEXT = new FreeplayGameContext();

    @Mock
    private GameScope gameScope;
    @Spy
    private Map<GameContextType, Provider<PlayerLifecycleHandler>> implementations = new HashMap<>();
    @Spy
    private TypeLiteral<PlayerLifecycleHandler> typeLiteral = TypeLiteral.get(PlayerLifecycleHandler.class);
    @InjectMocks
    private PlayerLifecycleHandlerProvider playerLifecycleHandlerProvider;

    @Test
    @DisplayName("get throws ComponentProvisionException when game scope has no current game context")
    void get_withoutEnteredGameContext() {
        when(gameScope.getCurrentGameContext()).thenReturn(Optional.empty());

        assertThatThrownBy(playerLifecycleHandlerProvider::get)
                .isInstanceOf(ComponentProvisionException.class)
                .hasMessage("Cannot provide instance of PlayerLifecycleHandler: the game scope is empty");
    }

    @Test
    @DisplayName("get throws ComponentProvisionException when provider contains no implementation type for game context type")
    void get_noImplementationTypesFound() {
        implementations.put(GameContextType.ARENA_MODE, mock());

        when(gameScope.getCurrentGameContext()).thenReturn(Optional.of(GAME_CONTEXT));

        assertThatThrownBy(playerLifecycleHandlerProvider::get)
                .isInstanceOf(ComponentProvisionException.class)
                .hasMessage("Cannot provide instance of PlayerLifecycleHandler: no implementation bound for FREEPLAY_MODE");
    }

    @Test
    @DisplayName("get returns instance bound to type of active game context")
    void get_successful() {
        PlayerLifecycleHandler playerLifecycleHandler = mock(PlayerLifecycleHandler.class);

        Provider<PlayerLifecycleHandler> freeplayPlayerLifecycleHandlerProvider = mock();
        when(freeplayPlayerLifecycleHandlerProvider.get()).thenReturn(playerLifecycleHandler);

        implementations.put(GameContextType.FREEPLAY_MODE, freeplayPlayerLifecycleHandlerProvider);

        when(gameScope.getCurrentGameContext()).thenReturn(Optional.of(GAME_CONTEXT));

        PlayerLifecycleHandler result = playerLifecycleHandlerProvider.get();

        assertThat(result).isEqualTo(playerLifecycleHandler);
    }
}
