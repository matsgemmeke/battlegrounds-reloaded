package nl.matsgemmeke.battlegrounds.command;

import co.aikar.commands.*;
import nl.matsgemmeke.battlegrounds.command.condition.FreeplayModePresenceCondition;
import nl.matsgemmeke.battlegrounds.command.tools.ToolsCommand;
import nl.matsgemmeke.battlegrounds.i18n.TextTemplate;
import nl.matsgemmeke.battlegrounds.i18n.Translator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommandBootstrapperTest {

    @Mock
    private PaperCommandManager commandManager;
    @Mock
    private CommandExtension commandExtension;
    @Spy
    private Set<CommandExtension> commandExtensions = new HashSet<>();
    @Mock
    private CommandConditions<BukkitCommandIssuer, BukkitCommandExecutionContext, BukkitConditionContext> commandConditions;
    @Mock
    private Translator translator;
    @Mock
    private BattlegroundsCommand bgCommand;
    @Mock
    private ToolsCommand toolsCommand;
    @Mock
    private FreeplayModePresenceCondition freeplayModePresenceCondition;
    @InjectMocks
    private CommandBootstrapper commandBootstrapper;

    @BeforeEach
    void setUp() {
        commandExtensions.add(commandExtension);

        when(commandManager.getCommandConditions()).thenReturn(commandConditions);
        when(translator.translate(anyString())).thenReturn(new TextTemplate("text"));
    }

    @Test
    @DisplayName("initialize registers all commands and conditions")
    void initialize() {
        commandBootstrapper.initialize();

        verify(bgCommand, times(6)).addCommandInfo(any(CommandInfo.class));
        verify(toolsCommand, times(1)).addCommandInfo(any(CommandInfo.class));

        verify(commandManager).registerCommand(bgCommand);
        verify(commandManager).registerCommand(toolsCommand);

        verify(commandConditions).addCondition("freeplay-mode-presence", freeplayModePresenceCondition);
    }
}
