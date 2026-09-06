package nl.matsgemmeke.battlegrounds.arena;

import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameContextType;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArenaRegistryTest {

    private static final int ARENA_ID = 1;
    private static final GameKey GAME_KEY = GameKey.ofArena(ARENA_ID);

    @Mock
    private GameContextProvider gameContextProvider;
    @InjectMocks
    private ArenaRegistry arenaRegistry;

    @Test
    @DisplayName("addArena registers arena in game context registry")
    void addArena() {
        Arena arena = mock(Arena.class);

        arenaRegistry.addArena(GAME_KEY, arena);

        ArgumentCaptor<GameContext> gameContextCaptor = ArgumentCaptor.forClass(GameContext.class);
        verify(gameContextProvider).addGameContext(eq(GAME_KEY), gameContextCaptor.capture());

        assertThat(gameContextCaptor.getValue()).satisfies(gameContext -> {
            assertThat(gameContext.getGameKey()).isEqualTo(GAME_KEY);
            assertThat(gameContext.getType()).isEqualTo(GameContextType.ARENA_MODE);
        });
    }

    @Test
    @DisplayName("getArena returns empty optional when given arena id is not registered")
    void getArena_unregistered() {
        Optional<Arena> arenaOptional = arenaRegistry.getArena(ARENA_ID);

        assertThat(arenaOptional).isEmpty();
    }

    @Test
    @DisplayName("getArena returns optional with corresponding arena")
    void getArena_registered() {
        GameKey gameKey = GameKey.ofArena(ARENA_ID);
        Arena arena = mock(Arena.class);

        arenaRegistry.addArena(gameKey, arena);
        Optional<Arena> arenaOptional = arenaRegistry.getArena(ARENA_ID);

        assertThat(arenaOptional).hasValue(arena);
    }

    @Test
    @DisplayName("getArenaIds returns all id's from registered arenas")
    void getArenaIds() {
        Arena arena = mock(Arena.class);
        when(arena.getId()).thenReturn(ARENA_ID);

        arenaRegistry.addArena(GameKey.ofArena(ARENA_ID), arena);
        List<Integer> arenaIds = arenaRegistry.getArenaIds();

        assertThat(arenaIds).containsExactly(ARENA_ID);
    }
}
