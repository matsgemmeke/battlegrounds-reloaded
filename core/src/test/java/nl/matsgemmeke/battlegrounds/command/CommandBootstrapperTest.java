package nl.matsgemmeke.battlegrounds.command;

import co.aikar.commands.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommandBootstrapperTest {

    @Mock
    private PaperCommandManager commandManager;
    @Mock
    private CommandExtension commandExtension;
    @Spy
    private Set<CommandExtension> commandExtensions = new HashSet<>();
    @InjectMocks
    private CommandBootstrapper commandBootstrapper;

    @Test
    @DisplayName("initialize registers all command extensions")
    void initialize() {
        commandExtensions.add(commandExtension);

        commandBootstrapper.initialize();

        verify(commandExtension).configure(commandManager);
    }
}
