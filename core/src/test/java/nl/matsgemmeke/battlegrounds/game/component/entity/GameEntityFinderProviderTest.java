package nl.matsgemmeke.battlegrounds.game.component.entity;

import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextType;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.game.component.ComponentProvisionException;
import nl.matsgemmeke.battlegrounds.game.component.entity.openmode.OpenModeGameEntityFinder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameEntityFinderProviderTest {

    private static final TypeLiteral<GameEntityFinder> TYPE_LITERAL = TypeLiteral.get(GameEntityFinder.class);

    @Mock
    private GameScope gameScope;

    @Test
    void getThrowsComponentProvisionExceptionWhenGameScopeHasNoEnteredGameContext() {
        Map<GameContextType, Provider<GameEntityFinder>> implementations = Map.of();

        when(gameScope.getCurrentGameContext()).thenReturn(Optional.empty());

        GameEntityFinderProvider provider = new GameEntityFinderProvider(gameScope, implementations, TYPE_LITERAL);

        assertThatThrownBy(provider::get)
                .isInstanceOf(ComponentProvisionException.class)
                .hasMessage("Cannot provide instance of GameEntityFinder: the game scope is empty");
    }

    @Test
    void getThrowsComponentProvisionExceptionWhenImplementationContainsNoProviderForGameContextType() {
        GameContext gameContext = new GameContext(GameKey.ofOpenMode(), GameContextType.ARENA_MODE);
        Map<GameContextType, Provider<GameEntityFinder>> implementations = Map.of(GameContextType.OPEN_MODE, mock());

        when(gameScope.getCurrentGameContext()).thenReturn(Optional.of(gameContext));

        GameEntityFinderProvider provider = new GameEntityFinderProvider(gameScope, implementations, TYPE_LITERAL);

        assertThatThrownBy(provider::get)
                .isInstanceOf(ComponentProvisionException.class)
                .hasMessage("Cannot provide instance of GameEntityFinder: no implementation bound for ARENA_MODE");
    }

    @Test
    void getReturnsInstanceBoundToTypeOfActiveGameContext() {
        GameContext gameContext = new GameContext(GameKey.ofOpenMode(), GameContextType.OPEN_MODE);
        OpenModeGameEntityFinder gameEntityFinder = mock(OpenModeGameEntityFinder.class);

        Provider<GameEntityFinder> gameEntityFinderProvider = mock();
        when(gameEntityFinderProvider.get()).thenReturn(gameEntityFinder);

        Map<GameContextType, Provider<GameEntityFinder>> implementations = Map.of(GameContextType.OPEN_MODE, gameEntityFinderProvider);

        when(gameScope.getCurrentGameContext()).thenReturn(Optional.of(gameContext));

        GameEntityFinderProvider provider = new GameEntityFinderProvider(gameScope, implementations, TYPE_LITERAL);
        GameEntityFinder result = provider.get();

        assertThat(result).isEqualTo(gameEntityFinder);
    }
}
