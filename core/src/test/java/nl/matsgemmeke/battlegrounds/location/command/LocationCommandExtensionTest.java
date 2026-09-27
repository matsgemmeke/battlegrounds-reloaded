package nl.matsgemmeke.battlegrounds.location.command;

import co.aikar.commands.PaperCommandManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LocationCommandExtensionTest {

    @Mock
    private MainLobbyCommand mainLobbyCommand;
    @Mock
    private PaperCommandManager commandManager;
    @InjectMocks
    private LocationCommandExtension commandExtension;

    @Test
    @DisplayName("configure adds commands to given command manager")
    void configure() {
        commandExtension.configure(commandManager);

        verify(commandManager).registerCommand(mainLobbyCommand);
    }
}
