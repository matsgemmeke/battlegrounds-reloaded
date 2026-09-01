package nl.matsgemmeke.battlegrounds.tools;

import co.aikar.commands.PaperCommandManager;
import nl.matsgemmeke.battlegrounds.command.CommandInfo;
import nl.matsgemmeke.battlegrounds.i18n.TextTemplate;
import nl.matsgemmeke.battlegrounds.i18n.Translator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ToolsCommandExtensionTest {

    @Mock
    private PaperCommandManager commandManager;
    @Mock
    private ToolsCommand toolsCommand;
    @Mock
    private Translator translator;
    @InjectMocks
    private ToolsCommandExtension commandExtension;

    @Test
    @DisplayName("configure initializes commands")
    void configure() {
        when(translator.translate(anyString())).thenReturn(new TextTemplate("text"));

        commandExtension.configure(commandManager);

        verify(toolsCommand, times(1)).addCommandInfo(any(CommandInfo.class));

        verify(commandManager).registerCommand(toolsCommand);
    }
}
