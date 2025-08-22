package nl.matsgemmeke.battlegrounds.game.component;

import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextType;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.game.openmode.component.OpenModeTargetFinder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TargetFinderProviderTest {

    private GameScope gameScope;
    private TypeLiteral<TargetFinder> typeLiteral;

    @BeforeEach
    public void setUp() {
        gameScope = mock(GameScope.class);
        typeLiteral = TypeLiteral.get(TargetFinder.class);
    }

    @Test
    public void getThrowsComponentProvisionExceptionWhenGameScopeHasNoEnteredGameContext() {
        Map<GameContextType, Provider<TargetFinder>> implementations = Map.of();

        when(gameScope.getCurrentGameContext()).thenReturn(Optional.empty());

        TargetFinderProvider provider = new TargetFinderProvider(gameScope, implementations, typeLiteral);

        assertThatThrownBy(provider::get)
                .isInstanceOf(ComponentProvisionException.class)
                .hasMessage("Cannot provide instance of TargetFinder: the game scope is empty");
    }

    @Test
    public void getThrowsComponentProvisionExceptionWhenImplementationContainsNoProviderForGameContextType() {
        GameContext gameContext = new GameContext(GameKey.ofOpenMode(), GameContextType.ARENA_MODE);
        Map<GameContextType, Provider<TargetFinder>> implementations = Map.of(GameContextType.OPEN_MODE, mock());

        when(gameScope.getCurrentGameContext()).thenReturn(Optional.of(gameContext));

        TargetFinderProvider provider = new TargetFinderProvider(gameScope, implementations, typeLiteral);

        assertThatThrownBy(provider::get)
                .isInstanceOf(ComponentProvisionException.class)
                .hasMessage("Cannot provide instance of TargetFinder: no implementation bound for ARENA_MODE");
    }

    @Test
    public void getReturnsInstanceBoundToTypeOfActiveGameContext() {
        GameContext gameContext = new GameContext(GameKey.ofOpenMode(), GameContextType.OPEN_MODE);
        OpenModeTargetFinder targetFinder = mock(OpenModeTargetFinder.class);

        Provider<TargetFinder> openModeTargetFinderProvider = mock();
        when(openModeTargetFinderProvider.get()).thenReturn(targetFinder);

        Map<GameContextType, Provider<TargetFinder>> implementations = Map.of(GameContextType.OPEN_MODE, openModeTargetFinderProvider);

        when(gameScope.getCurrentGameContext()).thenReturn(Optional.of(gameContext));

        TargetFinderProvider provider = new TargetFinderProvider(gameScope, implementations, typeLiteral);
        TargetFinder result = provider.get();

        assertThat(result).isEqualTo(targetFinder);
    }
}
