package nl.matsgemmeke.battlegrounds.game.component.entity;

import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import nl.matsgemmeke.battlegrounds.freeplay.FreeplayGameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextType;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.game.component.ComponentProvisionException;
import nl.matsgemmeke.battlegrounds.game.freeplay.component.entity.FreeplayMobRegistry;
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
class MobRegistryProviderTest {

    private static final FreeplayGameContext GAME_CONTEXT = new FreeplayGameContext();

    @Mock
    private GameScope gameScope;
    @Spy
    private Map<GameContextType, Provider<MobRegistry>> implementations = new HashMap<>();
    @Spy
    private TypeLiteral<MobRegistry> TYPE_LITERAL = TypeLiteral.get(MobRegistry.class);
    @InjectMocks
    private MobRegistryProvider mobRegistryProvider;

    @Test
    @DisplayName("get throws ComponentProvisionException when game scope has no entered game context")
    void get_noEnteredGameContext() {
        when(gameScope.getCurrentGameContext()).thenReturn(Optional.empty());

        assertThatThrownBy(mobRegistryProvider::get)
                .isInstanceOf(ComponentProvisionException.class)
                .hasMessage("Cannot provide instance of MobRegistry: the game scope is empty");
    }

    @Test
    @DisplayName("get throws ComponentProvisionException when implementations contains no provider for current game context type")
    void get_noCompatibleProvider() {
        implementations.put(GameContextType.ARENA_MODE, mock());

        when(gameScope.getCurrentGameContext()).thenReturn(Optional.of(GAME_CONTEXT));

        MobRegistryProvider provider = new MobRegistryProvider(gameScope, implementations, TYPE_LITERAL);

        assertThatThrownBy(provider::get)
                .isInstanceOf(ComponentProvisionException.class)
                .hasMessage("Cannot provide instance of MobRegistry: no implementation bound for FREEPLAY_MODE");
    }

    @Test
    @DisplayName("get returns provider bound to active game context type")
    void get_successful() {
        FreeplayMobRegistry mobRegistry = mock(FreeplayMobRegistry.class);

        Provider<MobRegistry> freeplayMobRegistryProvider = mock();
        when(freeplayMobRegistryProvider.get()).thenReturn(mobRegistry);

        implementations.put(GameContextType.FREEPLAY_MODE, freeplayMobRegistryProvider);

        when(gameScope.getCurrentGameContext()).thenReturn(Optional.of(GAME_CONTEXT));

        MobRegistry result = mobRegistryProvider.get();

        assertThat(result).isEqualTo(mobRegistry);
    }
}

