package nl.matsgemmeke.battlegrounds.arena.command.executor;

import nl.matsgemmeke.battlegrounds.arena.ArenaRegistry;
import nl.matsgemmeke.battlegrounds.arena.exception.ArenaNotFoundException;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MapListCommandExecutorTest {

    private static final int ARENA_ID = 1;

    @Mock
    private ArenaRegistry arenaRegistry;
    @Mock
    private Player player;
    @Mock
    private Translator translator;
    @InjectMocks
    private MapListCommandExecutor commandExecutor;

    @Test
    @DisplayName("execute throws ArenaNotFoundException when an arena by the given id somehow doesn't exist")
    void execute_arenaNotExists() {
        when(arenaRegistry.getArena(ARENA_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commandExecutor.execute(player, ARENA_ID))
                .isInstanceOf(ArenaNotFoundException.class)
                .hasMessage("Received a supposedly validated arena id 1, but the arena instance is not present");
    }
}
