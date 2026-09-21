package nl.matsgemmeke.battlegrounds.arena;

import com.google.inject.OutOfScopeException;
import nl.matsgemmeke.battlegrounds.freeplay.FreeplayGameContext;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArenaProviderTest {

    @Mock
    private GameScope gameScope;
    @InjectMocks
    private ArenaProvider arenaProvider;

    @Test
    @DisplayName("get throws OutOfScopeException when game scope has not entered a game context")
    void get_noEnteredGameContext() {
        when(gameScope.getCurrentGameContext()).thenReturn(Optional.empty());

        assertThatThrownBy(arenaProvider::get)
                .isInstanceOf(OutOfScopeException.class)
                .hasMessage("Unable to provide Arena instance as no game context is entered");
    }

    @Test
    @DisplayName("get throws IllegalStateException when entered game context is not of type arena")
    void get_enteredGameContextIsNotAnArena() {
        FreeplayGameContext gameContext = new FreeplayGameContext();

        when(gameScope.getCurrentGameContext()).thenReturn(Optional.of(gameContext));

        assertThatThrownBy(arenaProvider::get)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Arena requested but current GameContext (FREEPLAY) is not an arena context");
    }

    @Test
    @DisplayName("get returns Arena instance belonging to arena game context")
    void get_successful() {
        Arena arena = mock(Arena.class);
        ArenaGameContext gameContext = new ArenaGameContext(GameKey.ofArena(1), arena);

        when(gameScope.getCurrentGameContext()).thenReturn(Optional.of(gameContext));

        Arena result = arenaProvider.get();

        assertThat(result).isEqualTo(arena);
    }
}
