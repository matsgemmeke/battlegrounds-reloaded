package nl.matsgemmeke.battlegrounds.game.component.entity;

import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextType;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.game.component.ComponentProvisionException;
import nl.matsgemmeke.battlegrounds.game.openmode.component.entity.OpenModeMobRegistry;
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
class MobRegistryProviderTest {

    private static final TypeLiteral<MobRegistry> TYPE_LITERAL = TypeLiteral.get(MobRegistry.class);

    @Mock
    private GameScope gameScope;

    @Test
    void getThrowsComponentProvisionExceptionWhenGameScopeHasNoEnteredGameContext() {
        Map<GameContextType, Provider<MobRegistry>> implementations = Map.of();

        when(gameScope.getCurrentGameContext()).thenReturn(Optional.empty());

        MobRegistryProvider provider = new MobRegistryProvider(gameScope, implementations, TYPE_LITERAL);

        assertThatThrownBy(provider::get)
                .isInstanceOf(ComponentProvisionException.class)
                .hasMessage("Cannot provide instance of MobRegistry: the game scope is empty");
    }

    @Test
    void getThrowsComponentProvisionExceptionWhenImplementationContainsNoProviderForGameContextType() {
        GameContext gameContext = new GameContext(GameKey.ofOpenMode(), GameContextType.ARENA_MODE);
        Map<GameContextType, Provider<MobRegistry>> implementations = Map.of(GameContextType.OPEN_MODE, mock());

        when(gameScope.getCurrentGameContext()).thenReturn(Optional.of(gameContext));

        MobRegistryProvider provider = new MobRegistryProvider(gameScope, implementations, TYPE_LITERAL);

        assertThatThrownBy(provider::get)
                .isInstanceOf(ComponentProvisionException.class)
                .hasMessage("Cannot provide instance of MobRegistry: no implementation bound for ARENA_MODE");
    }

    @Test
    void getReturnsInstanceBoundToTypeOfActiveGameContext() {
        GameContext gameContext = new GameContext(GameKey.ofOpenMode(), GameContextType.OPEN_MODE);
        OpenModeMobRegistry mobRegistry = mock(OpenModeMobRegistry.class);

        Provider<MobRegistry> mobRegistryProvider = mock();
        when(mobRegistryProvider.get()).thenReturn(mobRegistry);

        Map<GameContextType, Provider<MobRegistry>> implementations = Map.of(GameContextType.OPEN_MODE, mobRegistryProvider);

        when(gameScope.getCurrentGameContext()).thenReturn(Optional.of(gameContext));

        MobRegistryProvider provider = new MobRegistryProvider(gameScope, implementations, TYPE_LITERAL);
        MobRegistry result = provider.get();

        assertThat(result).isEqualTo(mobRegistry);
    }
}

