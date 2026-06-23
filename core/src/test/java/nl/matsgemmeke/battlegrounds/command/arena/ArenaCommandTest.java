package nl.matsgemmeke.battlegrounds.command.arena;

import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ArenaCommandTest {

    private static final int ARENA_ID = 1;

    @Mock
    private CreateArenaExecutor createArenaExecutor;
    @Mock
    private RemoveArenaExecutor removeArenaExecutor;
    @InjectMocks
    private ArenaCommand command;

    @Test
    @DisplayName("onCreate delegates to create arena executor")
    void onCreate() {
        CommandSender sender = mock(CommandSender.class);

        command.onCreate(sender, ARENA_ID);

        verify(createArenaExecutor).execute(sender, ARENA_ID);
    }

    @Test
    @DisplayName("onRemove delegates to create arena executor")
    void onRemove() {
        CommandSender sender = mock(CommandSender.class);

        command.onRemove(sender, ARENA_ID);

        verify(removeArenaExecutor).execute(sender, ARENA_ID);
    }
}
