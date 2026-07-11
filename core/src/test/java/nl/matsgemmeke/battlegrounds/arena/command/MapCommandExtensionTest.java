package nl.matsgemmeke.battlegrounds.arena.command;

import co.aikar.commands.PaperCommandManager;
import nl.matsgemmeke.battlegrounds.command.CommandInfo;
import nl.matsgemmeke.battlegrounds.text.TextTemplate;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MapCommandExtensionTest {

    @Mock
    private MapCommand mapCommand;
    @Mock
    private PaperCommandManager commandManager;
    @Mock
    private Translator translator;
    @InjectMocks
    private MapCommandExtension commandExtension;

    @Test
    @DisplayName("configure initializes commands, command completions and command conditions")
    void configure() {
        when(translator.translate(anyString())).thenReturn(new TextTemplate("text"));

        commandExtension.configure(commandManager);

        verify(mapCommand, times(1)).addCommandInfo(any(CommandInfo.class));

        verify(commandManager).registerCommand(mapCommand);
    }
}
