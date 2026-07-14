package nl.matsgemmeke.battlegrounds.arena.command.completion;

import co.aikar.commands.BukkitCommandCompletionContext;
import nl.matsgemmeke.battlegrounds.arena.Arena;
import nl.matsgemmeke.battlegrounds.arena.ArenaRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MapNameCommandCompletionHandlerTest {

    private static final int ARENA_ID = 1;
    private static final String MAP_NAME = "Level 1";

    @Mock
    private ArenaRegistry arenaRegistry;
    @Mock
    private BukkitCommandCompletionContext context;
    @InjectMocks
    private MapNameCommandCompletionHandler commandCompletionHandler;

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("getCompletions returns empty list when arena id is invalid")
    void getCompletions_unprovidedArenaId(String arenaId) {
        when(context.getArgs()).thenReturn(Collections.singletonList(arenaId));

        Collection<String> completions = commandCompletionHandler.getCompletions(context);

        assertThat(completions).isEmpty();
    }

    @Test
    @DisplayName("getCompletions returns empty list when arena id is not a number")
    void getCompletions_invalidArenaId() {
        when(context.getArgs()).thenReturn(List.of("words"));

        Collection<String> completions = commandCompletionHandler.getCompletions(context);

        assertThat(completions).isEmpty();
    }

    @Test
    @DisplayName("getCompletions returns empty list when given arena id not registered")
    void getCompletions_unregisteredArenaId() {
        when(context.getArgs()).thenReturn(List.of(String.valueOf(ARENA_ID)));
        when(arenaRegistry.getArena(ARENA_ID)).thenReturn(Optional.empty());

        Collection<String> completions = commandCompletionHandler.getCompletions(context);

        assertThat(completions).isEmpty();
    }

    @Test
    @DisplayName("getCompletions returns list of map names from given registered arena")
    void getCompletions_successful() {
        Arena arena = mock(Arena.class);
        when(arena.getMapNames()).thenReturn(List.of(MAP_NAME));

        when(context.getArgs()).thenReturn(List.of(String.valueOf(ARENA_ID)));
        when(arenaRegistry.getArena(ARENA_ID)).thenReturn(Optional.of(arena));

        Collection<String> completions = commandCompletionHandler.getCompletions(context);

        assertThat(completions).containsExactly(MAP_NAME);
    }
}
