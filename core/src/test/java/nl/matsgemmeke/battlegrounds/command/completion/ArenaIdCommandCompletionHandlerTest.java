package nl.matsgemmeke.battlegrounds.command.completion;

import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArenaIdCommandCompletionHandlerTest {

    private static final List<Integer> ARENA_IDS = List.of(1, 2, 345);

    @Mock
    private GameContextProvider gameContextProvider;
    @InjectMocks
    private ArenaIdCommandCompletionHandler commandCompletionHandler;

    @Test
    @DisplayName("getCompletions returns arena id's from game context provider as strings")
    void getCompletions() {
        when(gameContextProvider.getArenaIds()).thenReturn(ARENA_IDS);

        Collection<String> result = commandCompletionHandler.getCompletions(null);

        assertThat(result).containsExactly("1", "2", "345");
    }
}
