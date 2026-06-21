package nl.matsgemmeke.battlegrounds.game.component.player;

import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextType;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.game.component.ComponentProvisionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PlayerLifecycleHandlerProviderTest {

    private GameScope gameScope;
    private TypeLiteral<PlayerLifecycleHandler> typeLiteral;

    @BeforeEach
    void setUp() {
        gameScope = mock(GameScope.class);
        typeLiteral = TypeLiteral.get(PlayerLifecycleHandler.class);
    }

    @Test
    @DisplayName("get throws ComponentProvisionException when game scope has no current game context")
    void get_withoutEnteredGameContext() {
        Map<GameContextType, Provider<PlayerLifecycleHandler>> implementations = Map.of();

        when(gameScope.getCurrentGameContext()).thenReturn(Optional.empty());

        PlayerLifecycleHandlerProvider provider = new PlayerLifecycleHandlerProvider(gameScope, implementations, typeLiteral);

        assertThatThrownBy(provider::get)
                .isInstanceOf(ComponentProvisionException.class)
                .hasMessage("Cannot provide instance of PlayerLifecycleHandler: the game scope is empty");
    }

    @Test
    @DisplayName("get throws ComponentProvisionException when provider contains no implementation type for game context type")
    void get_noImplementationTypesFound() {
        GameContext gameContext = new GameContext(GameKey.ofFreeplay(), GameContextType.ARENA_MODE);
        Map<GameContextType, Provider<PlayerLifecycleHandler>> implementations = Map.of(GameContextType.FREEPLAY_MODE, mock());

        when(gameScope.getCurrentGameContext()).thenReturn(Optional.of(gameContext));

        PlayerLifecycleHandlerProvider provider = new PlayerLifecycleHandlerProvider(gameScope, implementations, typeLiteral);

        assertThatThrownBy(provider::get)
                .isInstanceOf(ComponentProvisionException.class)
                .hasMessage("Cannot provide instance of PlayerLifecycleHandler: no implementation bound for ARENA_MODE");
    }

    @Test
    @DisplayName("get returns instance bound to type of active game context")
    void get_successful() {
        GameContext gameContext = new GameContext(GameKey.ofFreeplay(), GameContextType.FREEPLAY_MODE);
        PlayerLifecycleHandler playerLifecycleHandler = mock(PlayerLifecycleHandler.class);

        Provider<PlayerLifecycleHandler> freeplayPlayerLifecycleHandlerProvider = mock();
        when(freeplayPlayerLifecycleHandlerProvider.get()).thenReturn(playerLifecycleHandler);

        Map<GameContextType, Provider<PlayerLifecycleHandler>> implementations = Map.of(GameContextType.FREEPLAY_MODE, freeplayPlayerLifecycleHandlerProvider);

        when(gameScope.getCurrentGameContext()).thenReturn(Optional.of(gameContext));

        PlayerLifecycleHandlerProvider provider = new PlayerLifecycleHandlerProvider(gameScope, implementations, typeLiteral);
        PlayerLifecycleHandler result = provider.get();

        assertThat(result).isEqualTo(playerLifecycleHandler);
    }
}
