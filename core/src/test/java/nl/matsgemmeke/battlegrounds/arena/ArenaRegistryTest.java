package nl.matsgemmeke.battlegrounds.arena;

import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.arena.Arena;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ArenaRegistryTest {

    private static final int ARENA_ID = 1;

    @Mock
    private GameContextProvider gameContextProvider;
    @InjectMocks
    private ArenaRegistry arenaRegistry;

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
}
