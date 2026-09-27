package nl.matsgemmeke.battlegrounds.location.command;

import nl.matsgemmeke.battlegrounds.location.command.executor.SetMainLobbyCommandExecutor;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MainLobbyCommandTest {

    @Mock
    private Player player;
    @Mock
    private SetMainLobbyCommandExecutor setMainLobbyCommandExecutor;
    @InjectMocks
    private MainLobbyCommand mainLobbyCommand;

    @Test
    @DisplayName("onSet delegates to SetMainLobbyCommandExecutor")
    void onSet() {
        mainLobbyCommand.onSet(player);

        verify(setMainLobbyCommandExecutor).execute(player);
    }
}
