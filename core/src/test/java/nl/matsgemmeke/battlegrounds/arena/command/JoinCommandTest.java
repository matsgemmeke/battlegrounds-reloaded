package nl.matsgemmeke.battlegrounds.arena.command;

import nl.matsgemmeke.battlegrounds.arena.command.executor.JoinCommandExecutor;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class JoinCommandTest {

    private static final int ARENA_ID = 1;

    @Mock
    private JoinCommandExecutor joinCommandExecutor;
    @Mock
    private Player player;
    @InjectMocks
    private JoinCommand command;

    @Test
    @DisplayName("onJoin delegates to JoinCommandExecutor")
    void onJoin() {
        command.onJoin(player, ARENA_ID);

        verify(joinCommandExecutor).execute(player, ARENA_ID);
    }
}
