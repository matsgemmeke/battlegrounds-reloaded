package nl.matsgemmeke.battlegrounds.arena.command;

import co.aikar.commands.*;
import nl.matsgemmeke.battlegrounds.arena.command.completion.MapNameCommandCompletionHandler;
import nl.matsgemmeke.battlegrounds.arena.command.condition.ExistentMapNameCondition;
import nl.matsgemmeke.battlegrounds.arena.command.condition.NonexistentMapNameCondition;
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
class MapCommandExtensionTest {

    @Mock
    private CommandCompletions<BukkitCommandCompletionContext> commandCompletions;
    @Mock
    private CommandConditions<BukkitCommandIssuer, BukkitCommandExecutionContext, BukkitConditionContext> commandConditions;
    @Mock
    private MapCommand mapCommand;
    @Mock
    private MapNameCommandCompletionHandler mapNameCommandCompletionHandler;
    @Mock
    private ExistentMapNameCondition existentMapNameCondition;
    @Mock
    private NonexistentMapNameCondition nonexistentMapNameCondition;
    @Mock
    private PaperCommandManager commandManager;
    @Mock
    private Translator translator;
    @InjectMocks
    private MapCommandExtension commandExtension;

    @Test
    @DisplayName("configure initializes commands, command completions and command conditions")
    void configure() {
        when(commandManager.getCommandCompletions()).thenReturn(commandCompletions);
        when(commandManager.getCommandConditions()).thenReturn(commandConditions);
        when(translator.translate(anyString())).thenReturn(new TextTemplate("text"));

        commandExtension.configure(commandManager);

        verify(mapCommand, times(3)).addCommandInfo(any(CommandInfo.class));

        verify(commandManager).registerCommand(mapCommand);

        verify(commandCompletions).registerCompletion("map-name", mapNameCommandCompletionHandler);

        verify(commandConditions).addCondition(String.class, "existent-map-name", existentMapNameCondition);
        verify(commandConditions).addCondition(String.class, "nonexistent-map-name", nonexistentMapNameCondition);
    }
}
