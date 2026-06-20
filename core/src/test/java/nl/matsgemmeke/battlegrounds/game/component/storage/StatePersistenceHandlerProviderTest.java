package nl.matsgemmeke.battlegrounds.game.component.storage;

import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextType;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.game.component.ComponentProvisionException;
import nl.matsgemmeke.battlegrounds.game.freeplay.component.storage.FreeplayStatePersistenceHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StatePersistenceHandlerProviderTest {

    private GameScope gameScope;
    private TypeLiteral<StatePersistenceHandler> typeLiteral;

    @BeforeEach
    public void setUp() {
        gameScope = mock(GameScope.class);
        typeLiteral = TypeLiteral.get(StatePersistenceHandler.class);
    }

    @Test
    public void getThrowsComponentProvisionExceptionWhenGameScopeHasNoEnteredGameContext() {
        Map<GameContextType, Provider<StatePersistenceHandler>> implementations = Map.of();

        when(gameScope.getCurrentGameContext()).thenReturn(Optional.empty());

        StatePersistenceHandlerProvider provider = new StatePersistenceHandlerProvider(gameScope, implementations, typeLiteral);

        assertThatThrownBy(provider::get)
                .isInstanceOf(ComponentProvisionException.class)
                .hasMessage("Cannot provide instance of StatePersistenceHandler: the game scope is empty");
    }

    @Test
    public void getThrowsComponentProvisionExceptionWhenImplementationContainsNoProviderForGameContextType() {
        GameContext gameContext = new GameContext(GameKey.ofFreeplay(), GameContextType.ARENA_MODE);
        Map<GameContextType, Provider<StatePersistenceHandler>> implementations = Map.of(GameContextType.FREEPLAY_MODE, mock());

        when(gameScope.getCurrentGameContext()).thenReturn(Optional.of(gameContext));

        StatePersistenceHandlerProvider provider = new StatePersistenceHandlerProvider(gameScope, implementations, typeLiteral);

        assertThatThrownBy(provider::get)
                .isInstanceOf(ComponentProvisionException.class)
                .hasMessage("Cannot provide instance of StatePersistenceHandler: no implementation bound for ARENA_MODE");
    }

    @Test
    public void getReturnsInstanceBoundToTypeOfActiveGameContext() {
        GameContext gameContext = new GameContext(GameKey.ofFreeplay(), GameContextType.FREEPLAY_MODE);
        FreeplayStatePersistenceHandler statePersistenceHandler = mock(FreeplayStatePersistenceHandler.class);

        Provider<StatePersistenceHandler> openModeStatePersistenceHandlerProvider = mock();
        when(openModeStatePersistenceHandlerProvider.get()).thenReturn(statePersistenceHandler);

        Map<GameContextType, Provider<StatePersistenceHandler>> implementations = Map.of(GameContextType.FREEPLAY_MODE, openModeStatePersistenceHandlerProvider);

        when(gameScope.getCurrentGameContext()).thenReturn(Optional.of(gameContext));

        StatePersistenceHandlerProvider provider = new StatePersistenceHandlerProvider(gameScope, implementations, typeLiteral);
        StatePersistenceHandler result = provider.get();

        assertThat(result).isEqualTo(statePersistenceHandler);
    }
}
