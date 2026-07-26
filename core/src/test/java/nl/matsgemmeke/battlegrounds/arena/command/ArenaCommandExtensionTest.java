package nl.matsgemmeke.battlegrounds.arena.command;

import co.aikar.commands.*;
import nl.matsgemmeke.battlegrounds.arena.command.completion.ArenaIdCommandCompletionHandler;
import nl.matsgemmeke.battlegrounds.arena.command.completion.MapNameCommandCompletionHandler;
import nl.matsgemmeke.battlegrounds.arena.command.condition.*;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArenaCommandExtensionTest {

    @Mock
    private ArenaCommand arenaCommand;
    @Mock
    private ElementCommand elementCommand;
    @Mock
    private MapCommand mapCommand;

    @Mock
    private ArenaIdCommandCompletionHandler arenaIdCommandCompletionHandler;
    @Mock
    private MapNameCommandCompletionHandler mapNameCommandCompletionHandler;

    @Mock
    private ExistentArenaIdCondition existentArenaIdCondition;
    @Mock
    private NonexistentArenaIdCondition nonexistentArenaIdCondition;
    @Mock
    private ExistentMapNameCondition existentMapNameCondition;
    @Mock
    private NonexistentMapNameCondition nonexistentMapNameCondition;
    @Mock
    private MapSelectedCondition mapSelectedCondition;

    @Mock
    private CommandCompletions<BukkitCommandCompletionContext> commandCompletions;
    @Mock
    private CommandConditions<BukkitCommandIssuer, BukkitCommandExecutionContext, BukkitConditionContext> commandConditions;
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
        verify(mapCommand, times(3)).addCommandInfo(any(CommandInfo.class));
        verify(elementCommand, times(1)).addCommandInfo(any(CommandInfo.class));

        verify(commandManager).registerCommand(arenaCommand);
        verify(commandManager).registerCommand(elementCommand);
        verify(commandManager).registerCommand(mapCommand);

        verify(commandCompletions).registerCompletion("arena-id", arenaIdCommandCompletionHandler);
        verify(commandCompletions).registerCompletion("map-name", mapNameCommandCompletionHandler);

        verify(commandConditions).addCondition(Integer.class, "existent-arena-id", existentArenaIdCondition);
        verify(commandConditions).addCondition(Integer.class, "nonexistent-arena-id", nonexistentArenaIdCondition);
        verify(commandConditions).addCondition(String.class, "existent-map-name", existentMapNameCondition);
        verify(commandConditions).addCondition(String.class, "nonexistent-map-name", nonexistentMapNameCondition);
        verify(commandConditions).addCondition("map-selected", mapSelectedCondition);
    }
}
