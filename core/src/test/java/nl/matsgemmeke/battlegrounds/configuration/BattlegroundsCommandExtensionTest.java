package nl.matsgemmeke.battlegrounds.configuration;

import co.aikar.commands.*;
import nl.matsgemmeke.battlegrounds.command.BattlegroundsCommand;
import nl.matsgemmeke.battlegrounds.command.BattlegroundsCommandExtension;
import nl.matsgemmeke.battlegrounds.command.CommandInfo;
import nl.matsgemmeke.battlegrounds.command.condition.FreeplayModePresenceCondition;
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
class BattlegroundsCommandExtensionTest {

    @Mock
    private BattlegroundsCommand bgCommand;
    @Mock
    private CommandConditions<BukkitCommandIssuer, BukkitCommandExecutionContext, BukkitConditionContext> commandConditions;
    @Mock
    private FreeplayModePresenceCondition freeplayModePresenceCondition;
    @Mock
    private PaperCommandManager commandManager;
    @Mock
    private Translator translator;
    @InjectMocks
    private BattlegroundsCommandExtension commandExtension;

    @Test
    @DisplayName("configure initializes commands")
    void configure() {
        when(commandManager.getCommandConditions()).thenReturn(commandConditions);
        when(translator.translate(anyString())).thenReturn(new TextTemplate("text"));

        commandExtension.configure(commandManager);

        verify(bgCommand, times(7)).addCommandInfo(any(CommandInfo.class));
        verify(commandConditions).addCondition("freeplay-mode-presence", freeplayModePresenceCondition);
    }
}
