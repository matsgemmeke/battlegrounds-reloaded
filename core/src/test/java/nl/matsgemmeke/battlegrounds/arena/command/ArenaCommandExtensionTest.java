package nl.matsgemmeke.battlegrounds.arena.command;

import co.aikar.commands.*;
import nl.matsgemmeke.battlegrounds.arena.command.completion.ArenaIdCommandCompletionHandler;
import nl.matsgemmeke.battlegrounds.arena.command.condition.ExistentArenaIdCondition;
import nl.matsgemmeke.battlegrounds.arena.command.condition.NonexistentArenaIdCondition;
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
class ArenaCommandExtensionTest {

    @Mock
    private ArenaCommand arenaCommand;
    @Mock
    private ArenaIdCommandCompletionHandler arenaIdCommandCompletionHandler;
    @Mock
    private CommandCompletions<BukkitCommandCompletionContext> commandCompletions;
    @Mock
    private CommandConditions<BukkitCommandIssuer, BukkitCommandExecutionContext, BukkitConditionContext> commandConditions;
    @Mock
    private ExistentArenaIdCondition existentArenaIdCondition;
    @Mock
    private NonexistentArenaIdCondition nonexistentArenaIdCondition;
    @Mock
    private PaperCommandManager commandManager;
    @Mock
    private Translator translator;
    @InjectMocks
    private ArenaCommandExtension commandExtension;

    @Test
    @DisplayName("configure initializes commands, command completions and command conditions")
    void configure() {
        when(commandManager.getCommandCompletions()).thenReturn(commandCompletions);
        when(commandManager.getCommandConditions()).thenReturn(commandConditions);
        when(translator.translate(anyString())).thenReturn(new TextTemplate("text"));

        commandExtension.configure(commandManager);

        verify(arenaCommand, times(3)).addCommandInfo(any(CommandInfo.class));

        verify(commandManager).registerCommand(arenaCommand);

        verify(commandCompletions).registerCompletion("arena-id", arenaIdCommandCompletionHandler);

        verify(commandConditions).addCondition(Integer.class, "existent-arena-id", existentArenaIdCondition);
        verify(commandConditions).addCondition(Integer.class, "nonexistent-arena-id", nonexistentArenaIdCondition);
    }
}
