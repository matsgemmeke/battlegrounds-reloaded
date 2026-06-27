package nl.matsgemmeke.battlegrounds.command;

import co.aikar.commands.*;
import nl.matsgemmeke.battlegrounds.command.arena.ArenaCommand;
import nl.matsgemmeke.battlegrounds.command.completion.ArenaIdCommandCompletionHandler;
import nl.matsgemmeke.battlegrounds.command.condition.ExistentArenaIdCondition;
import nl.matsgemmeke.battlegrounds.command.condition.FreeplayModePresenceCondition;
import nl.matsgemmeke.battlegrounds.command.condition.NonexistentArenaIdCondition;
import nl.matsgemmeke.battlegrounds.command.map.MapCommand;
import nl.matsgemmeke.battlegrounds.command.tools.ToolsCommand;
import nl.matsgemmeke.battlegrounds.text.TextTemplate;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommandBootstrapperTest {

    @Mock
    private PaperCommandManager commandManager;
    @Mock
    private CommandCompletions<BukkitCommandCompletionContext> commandCompletions;
    @Mock
    private CommandConditions<BukkitCommandIssuer, BukkitCommandExecutionContext, BukkitConditionContext> commandConditions;
    @Mock
    private Translator translator;
    @Mock
    private ArenaCommand arenaCommand;
    @Mock
    private BattlegroundsCommand bgCommand;
    @Mock
    private MapCommand mapCommand;
    @Mock
    private ToolsCommand toolsCommand;
    @Mock
    private ArenaIdCommandCompletionHandler arenaIdCommandCompletionHandler;
    @Mock
    private FreeplayModePresenceCondition freeplayModePresenceCondition;
    @Mock
    private ExistentArenaIdCondition existentArenaIdCondition;
    @Mock
    private NonexistentArenaIdCondition nonexistentArenaIdCondition;
    @InjectMocks
    private CommandBootstrapper commandBootstrapper;

    @BeforeEach
    void setUp() {
        when(commandManager.getCommandCompletions()).thenReturn(commandCompletions);
        when(commandManager.getCommandConditions()).thenReturn(commandConditions);
        when(translator.translate(anyString())).thenReturn(new TextTemplate("text"));
    }

    @Test
    @DisplayName("initialize registers all commands and conditions")
    void initialize() {
        commandBootstrapper.initialize();

        verify(bgCommand, times(5)).addCommandInfo(any(CommandInfo.class));
        verify(arenaCommand, times(3)).addCommandInfo(any(CommandInfo.class));
        verify(mapCommand, times(1)).addCommandInfo(any(CommandInfo.class));
        verify(toolsCommand, times(1)).addCommandInfo(any(CommandInfo.class));

        verify(commandManager).registerCommand(bgCommand);
        verify(commandManager).registerCommand(arenaCommand);
        verify(commandManager).registerCommand(toolsCommand);

        verify(commandCompletions).registerCompletion("arena-id", arenaIdCommandCompletionHandler);

        verify(commandConditions).addCondition("freeplay-mode-presence", freeplayModePresenceCondition);
        verify(commandConditions).addCondition(Integer.class, "existent-arena-id", existentArenaIdCondition);
        verify(commandConditions).addCondition(Integer.class, "nonexistent-arena-id", nonexistentArenaIdCondition);
    }
}
