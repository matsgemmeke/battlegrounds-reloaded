package nl.matsgemmeke.battlegrounds.game.component.targeting;

import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import nl.matsgemmeke.battlegrounds.freeplay.FreeplayGameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextType;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.game.component.ComponentProvisionException;
import nl.matsgemmeke.battlegrounds.game.freeplay.component.FreeplayTargetFinder;
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
class TargetFinderProviderTest {

    private static final FreeplayGameContext GAME_CONTEXT = new FreeplayGameContext();

    @Mock
    private GameScope gameScope;
    @Spy
    private Map<GameContextType, Provider<TargetFinder>> providers = new HashMap<>();
    @Spy
    private TypeLiteral<TargetFinder> typeLiteral = TypeLiteral.get(TargetFinder.class);
    @InjectMocks
    private TargetFinderProvider targetFinderProvider;

    @Test
    @DisplayName("get throws ComponentProvisionException when game scope has no entered game context")
    void get_noEnteredGameContext() {
        when(gameScope.getCurrentGameContext()).thenReturn(Optional.empty());

        assertThatThrownBy(targetFinderProvider::get)
                .isInstanceOf(ComponentProvisionException.class)
                .hasMessage("Cannot provide instance of TargetFinder: the game scope is empty");
    }

    @Test
    @DisplayName("get throws ComponentProvisionException when no provider is available for entered game context type")
    void get_noCompatibleProvider() {
        providers.put(GameContextType.ARENA_MODE, mock());

        when(gameScope.getCurrentGameContext()).thenReturn(Optional.of(GAME_CONTEXT));

        assertThatThrownBy(targetFinderProvider::get)
                .isInstanceOf(ComponentProvisionException.class)
                .hasMessage("Cannot provide instance of TargetFinder: no implementation bound for FREEPLAY_MODE");
    }

    @Test
    @DisplayName("get returns instance bound to entered game context type")
    void get_successful() {
        FreeplayTargetFinder targetFinder = mock(FreeplayTargetFinder.class);

        Provider<TargetFinder> freeplayTargetFinderProvider = mock();
        when(freeplayTargetFinderProvider.get()).thenReturn(targetFinder);

        providers.put(GameContextType.FREEPLAY_MODE, freeplayTargetFinderProvider);

        when(gameScope.getCurrentGameContext()).thenReturn(Optional.of(GAME_CONTEXT));

        TargetFinder result = targetFinderProvider.get();

        assertThat(result).isEqualTo(targetFinder);
    }
}
