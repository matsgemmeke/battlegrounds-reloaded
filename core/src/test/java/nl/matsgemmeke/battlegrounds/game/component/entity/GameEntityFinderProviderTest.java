package nl.matsgemmeke.battlegrounds.game.component.entity;

import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import nl.matsgemmeke.battlegrounds.freeplay.FreeplayGameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextType;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.game.component.ComponentProvisionException;
import nl.matsgemmeke.battlegrounds.game.component.entity.freeplay.FreeplayGameEntityFinder;
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
class GameEntityFinderProviderTest {

    private static final FreeplayGameContext GAME_CONTEXT = new FreeplayGameContext();

    @Mock
    private GameScope gameScope;
    @Spy
    private Map<GameContextType, Provider<GameEntityFinder>> providers = new HashMap<>();
    @Spy
    private TypeLiteral<GameEntityFinder> TYPE_LITERAL = TypeLiteral.get(GameEntityFinder.class);
    @InjectMocks
    private GameEntityFinderProvider gameEntityFinderProvider;

    @Test
    @DisplayName("get throws ComponentProvisionException when game scope has no entered game context")
    void get_noEnteredGameContext() {
        when(gameScope.getCurrentGameContext()).thenReturn(Optional.empty());

        assertThatThrownBy(gameEntityFinderProvider::get)
                .isInstanceOf(ComponentProvisionException.class)
                .hasMessage("Cannot provide instance of GameEntityFinder: the game scope is empty");
    }

    @Test
    @DisplayName("get throws ComponentProvisionException when no provider is available for current game context")
    void get_noCompatibleProvider() {
        providers.put(GameContextType.ARENA_MODE, mock());

        when(gameScope.getCurrentGameContext()).thenReturn(Optional.of(GAME_CONTEXT));

        assertThatThrownBy(gameEntityFinderProvider::get)
                .isInstanceOf(ComponentProvisionException.class)
                .hasMessage("Cannot provide instance of GameEntityFinder: no implementation bound for FREEPLAY_MODE");
    }

    @Test
    @DisplayName("get returns instance bound to active game context type")
    void get_successful() {
        FreeplayGameEntityFinder gameEntityFinder = mock(FreeplayGameEntityFinder.class);

        Provider<GameEntityFinder> freeplayGameEntityFinderProvider = mock();
        when(freeplayGameEntityFinderProvider.get()).thenReturn(gameEntityFinder);

        providers.put(GameContextType.FREEPLAY_MODE, freeplayGameEntityFinderProvider);

        when(gameScope.getCurrentGameContext()).thenReturn(Optional.of(GAME_CONTEXT));

        GameEntityFinder result = gameEntityFinderProvider.get();

        assertThat(result).isEqualTo(gameEntityFinder);
    }
}
