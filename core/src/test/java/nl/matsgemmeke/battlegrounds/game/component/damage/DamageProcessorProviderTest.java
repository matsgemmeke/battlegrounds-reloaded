package nl.matsgemmeke.battlegrounds.game.component.damage;

import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextType;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.game.component.ComponentProvisionException;
import nl.matsgemmeke.battlegrounds.game.openmode.component.damage.OpenModeDamageProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DamageProcessorProviderTest {

    private GameScope gameScope;
    private TypeLiteral<DamageProcessor> typeLiteral;

    @BeforeEach
    public void setUp() {
        gameScope = mock(GameScope.class);
        typeLiteral = TypeLiteral.get(DamageProcessor.class);
    }

    @Test
    public void getThrowsOutOfScopeExceptionWhenGameScopeHasNoEnteredGameContext() {
        Map<GameContextType, Provider<DamageProcessor>> implementations = Map.of();

        when(gameScope.getCurrentGameContext()).thenReturn(Optional.empty());

        DamageProcessorProvider provider = new DamageProcessorProvider(gameScope, implementations, typeLiteral);

        assertThatThrownBy(provider::get)
                .isInstanceOf(ComponentProvisionException.class)
                .hasMessage("Cannot provide instance of DamageProcessor: the game scope is empty");
    }

    @Test
    public void getThrowsIllegalStateExceptionWhenGameScopeHasNoEnteredGameContext() {
        GameContext gameContext = new GameContext(GameKey.ofOpenMode(), GameContextType.ARENA_MODE);
        Map<GameContextType, Provider<DamageProcessor>> implementations = Map.of(GameContextType.OPEN_MODE, mock());

        when(gameScope.getCurrentGameContext()).thenReturn(Optional.of(gameContext));

        DamageProcessorProvider provider = new DamageProcessorProvider(gameScope, implementations, typeLiteral);

        assertThatThrownBy(provider::get)
                .isInstanceOf(ComponentProvisionException.class)
                .hasMessage("Cannot provide instance of DamageProcessor: no implementation bound for ARENA_MODE");
    }

    @Test
    public void getReturnsInstanceBoundToTypeOfActiveGameContext() {
        GameContext gameContext = new GameContext(GameKey.ofOpenMode(), GameContextType.OPEN_MODE);
        OpenModeDamageProcessor damageProcessor = mock(OpenModeDamageProcessor.class);

        Provider<DamageProcessor> openModeDamageProcessorProvider = mock();
        when(openModeDamageProcessorProvider.get()).thenReturn(damageProcessor);

        Map<GameContextType, Provider<DamageProcessor>> implementations = Map.of(GameContextType.OPEN_MODE, openModeDamageProcessorProvider);

        when(gameScope.getCurrentGameContext()).thenReturn(Optional.of(gameContext));

        DamageProcessorProvider provider = new DamageProcessorProvider(gameScope, implementations, typeLiteral);
        DamageProcessor result = provider.get();

        assertThat(result).isEqualTo(damageProcessor);
    }
}
