package nl.matsgemmeke.battlegrounds.location.command;

import co.aikar.commands.*;
import nl.matsgemmeke.battlegrounds.command.CommandInfo;
import nl.matsgemmeke.battlegrounds.i18n.TextTemplate;
import nl.matsgemmeke.battlegrounds.i18n.Translator;
import nl.matsgemmeke.battlegrounds.location.command.condition.ExistentMainLobbyCondition;
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
class LocationCommandExtensionTest {

    @Mock
    private MainLobbyCommand mainLobbyCommand;

    @Mock
    private ExistentMainLobbyCondition existentMainLobbyCondition;

    @Mock
    private CommandConditions<BukkitCommandIssuer, BukkitCommandExecutionContext, BukkitConditionContext> commandConditions;
    @Mock
    private PaperCommandManager commandManager;
    @Mock
    private Translator translator;
    @InjectMocks
    private LocationCommandExtension commandExtension;

    @Test
    @DisplayName("configure adds commands to given command manager")
    void configure() {
        when(commandManager.getCommandConditions()).thenReturn(commandConditions);
        when(translator.translate(anyString())).thenReturn(new TextTemplate("text"));

        commandExtension.configure(commandManager);

        verify(mainLobbyCommand, times(2)).addCommandInfo(any(CommandInfo.class));
        verify(commandManager).registerCommand(mainLobbyCommand);
        verify(commandConditions).addCondition("existent-main-lobby", existentMainLobbyCondition);
    }
}
