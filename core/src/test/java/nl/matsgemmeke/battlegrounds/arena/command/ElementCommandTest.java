package nl.matsgemmeke.battlegrounds.arena.command;

import nl.matsgemmeke.battlegrounds.arena.command.executor.element.AddSpawnPointCommandExecutor;
import nl.matsgemmeke.battlegrounds.command.CommandInfo;
import nl.matsgemmeke.battlegrounds.command.HelpMenu;
import nl.matsgemmeke.battlegrounds.i18n.TextTemplate;
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ElementCommandTest {

    private static final int TEAM_ID = 1;
    private static final String UNKNOWN_COMMAND_TEXT = "unknown command";
    private static final String ELEMENT_HELP_MENU_HEADER_TEXT = "header";

    @Mock
    private AddSpawnPointCommandExecutor addSpawnPointCommandExecutor;
    @Mock
    private HelpMenu helpMenu;
    @Mock
    private Player player;
    @Mock
    private Translator translator;
    @InjectMocks
    private ElementCommand command;

    @Test
    @DisplayName("onDefault sends unknown command message when command has args")
    void onDefault_withArgs() {
        String[] args = new String[] { "test" };

        when(translator.translate(TranslationKey.UNKNOWN_COMMAND.getPath())).thenReturn(new TextTemplate(UNKNOWN_COMMAND_TEXT));

        command.onDefault(player, args);

        verify(player).sendMessage(UNKNOWN_COMMAND_TEXT);
    }

    @Test
    @DisplayName("onDefault shows help menu to player as JSON messages")
    void onDefault_playerSender() {
        CommandInfo commandInfo = new CommandInfo(null, null, null, new String[0]);

        when(translator.translate(TranslationKey.ELEMENT_HELP_MENU_HEADER.getPath())).thenReturn(new TextTemplate(ELEMENT_HELP_MENU_HEADER_TEXT));

        command.addCommandInfo(commandInfo);
        command.onDefault(player, null);

        verify(helpMenu).sendHelpMenuAsJsonMessages(player, ELEMENT_HELP_MENU_HEADER_TEXT, List.of(commandInfo));
    }

    @Test
    @DisplayName("onDefault shows help menu to sender as normal messages")
    void onDefault_consoleSender() {
        CommandInfo commandInfo = new CommandInfo(null, null, null, new String[0]);
        CommandSender sender = mock(CommandSender.class);

        when(translator.translate(TranslationKey.ELEMENT_HELP_MENU_HEADER.getPath())).thenReturn(new TextTemplate(ELEMENT_HELP_MENU_HEADER_TEXT));

        command.addCommandInfo(commandInfo);
        command.onDefault(sender, null);

        verify(helpMenu).sendHelpMenuAsNormalMessages(sender, ELEMENT_HELP_MENU_HEADER_TEXT, List.of(commandInfo));
    }

    @Test
    @DisplayName("onAddSpawnPoint delegates to AddSpawnPointCommandExecutor")
    void onSelect() {
        command.onAddSpawnPoint(player, TEAM_ID);

        verify(addSpawnPointCommandExecutor).execute(player, TEAM_ID);
    }
}
