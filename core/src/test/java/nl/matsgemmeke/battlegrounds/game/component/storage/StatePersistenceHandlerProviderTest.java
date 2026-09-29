package nl.matsgemmeke.battlegrounds.game.component.storage;

import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import nl.matsgemmeke.battlegrounds.freeplay.FreeplayGameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextType;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.game.component.ComponentProvisionException;
import nl.matsgemmeke.battlegrounds.game.freeplay.component.storage.FreeplayStatePersistenceHandler;
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
class StatePersistenceHandlerProviderTest {

    private static final FreeplayGameContext GAME_CONTEXT = new FreeplayGameContext();

    @Mock
    private GameScope gameScope;
    @Spy
    private Map<GameContextType, Provider<StatePersistenceHandler>> providers = new HashMap<>();
    @Spy
    private TypeLiteral<StatePersistenceHandler> typeLiteral = TypeLiteral.get(StatePersistenceHandler.class);
    @InjectMocks
    private StatePersistenceHandlerProvider statePersistenceHandlerProvider;

    @Test
    @DisplayName("get throws ComponentProvisionException when game scope has no entered game context")
    void get_noEnteredGameContext() {
        when(gameScope.getCurrentGameContext()).thenReturn(Optional.empty());

        assertThatThrownBy(statePersistenceHandlerProvider::get)
                .isInstanceOf(ComponentProvisionException.class)
                .hasMessage("Cannot provide instance of StatePersistenceHandler: the game scope is empty");
    }

    @Test
    @DisplayName("get throws ComponentProvisionException when no provider is available for game context type")
    void get_noCompatibleProvider() {
        providers.put(GameContextType.ARENA_MODE, mock());

        when(gameScope.getCurrentGameContext()).thenReturn(Optional.of(GAME_CONTEXT));

        assertThatThrownBy(statePersistenceHandlerProvider::get)
                .isInstanceOf(ComponentProvisionException.class)
                .hasMessage("Cannot provide instance of StatePersistenceHandler: no implementation bound for FREEPLAY_MODE");
    }

    @Test
    @DisplayName("get returns instance bound to game context type")
    void get_successful() {
        FreeplayStatePersistenceHandler statePersistenceHandler = mock(FreeplayStatePersistenceHandler.class);

        Provider<StatePersistenceHandler> freeplayStatePersistenceHandlerProvider = mock();
        when(freeplayStatePersistenceHandlerProvider.get()).thenReturn(statePersistenceHandler);

        providers.put(GameContextType.FREEPLAY_MODE, freeplayStatePersistenceHandlerProvider);

        when(gameScope.getCurrentGameContext()).thenReturn(Optional.of(GAME_CONTEXT));

        StatePersistenceHandler result = statePersistenceHandlerProvider.get();

        assertThat(result).isEqualTo(statePersistenceHandler);
    }
}
