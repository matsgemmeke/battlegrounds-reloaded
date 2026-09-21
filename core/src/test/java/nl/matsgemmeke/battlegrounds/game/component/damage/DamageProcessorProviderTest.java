package nl.matsgemmeke.battlegrounds.game.component.damage;

import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import nl.matsgemmeke.battlegrounds.freeplay.FreeplayGameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextType;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.game.component.ComponentProvisionException;
import nl.matsgemmeke.battlegrounds.game.freeplay.component.damage.FreeplayDamageProcessor;
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
class DamageProcessorProviderTest {

    private static final FreeplayGameContext GAME_CONTEXT = new FreeplayGameContext();

    @Mock
    private GameScope gameScope;
    @Spy
    private Map<GameContextType, Provider<DamageProcessor>> providers = new HashMap<>();
    @Spy
    private TypeLiteral<DamageProcessor> typeLiteral = TypeLiteral.get(DamageProcessor.class);
    @InjectMocks
    private DamageProcessorProvider damageProcessorProvider;

    @Test
    @DisplayName("get throws ComponentProvisionException when game scope has no entered game context")
    void get_noEnteredGameScope() {
        when(gameScope.getCurrentGameContext()).thenReturn(Optional.empty());

        assertThatThrownBy(damageProcessorProvider::get)
                .isInstanceOf(ComponentProvisionException.class)
                .hasMessage("Cannot provide instance of DamageProcessor: the game scope is empty");
    }

    @Test
    @DisplayName("get throws ComponentProvisionException when no provider is available for game context type")
    void get_noCompatibleProvider() {
        providers.put(GameContextType.ARENA_MODE, mock());

        when(gameScope.getCurrentGameContext()).thenReturn(Optional.of(GAME_CONTEXT));

        assertThatThrownBy(damageProcessorProvider::get)
                .isInstanceOf(ComponentProvisionException.class)
                .hasMessage("Cannot provide instance of DamageProcessor: no implementation bound for FREEPLAY_MODE");
    }

    @Test
    @DisplayName("get returns instance bound to game context type")
    void get_successful() {
        FreeplayDamageProcessor damageProcessor = mock(FreeplayDamageProcessor.class);

        Provider<DamageProcessor> freeplayDamageProcessorProvider = mock();
        when(freeplayDamageProcessorProvider.get()).thenReturn(damageProcessor);

        providers.put(GameContextType.FREEPLAY_MODE, freeplayDamageProcessorProvider);

        when(gameScope.getCurrentGameContext()).thenReturn(Optional.of(GAME_CONTEXT));

        DamageProcessor result = damageProcessorProvider.get();

        assertThat(result).isEqualTo(damageProcessor);
    }
}
