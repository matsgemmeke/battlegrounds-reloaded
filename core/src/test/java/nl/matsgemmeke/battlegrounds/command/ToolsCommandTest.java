package nl.matsgemmeke.battlegrounds.command;

import nl.matsgemmeke.battlegrounds.command.tool.ShowHitboxesTool;
import nl.matsgemmeke.battlegrounds.command.tool.ToolsCommand;
import nl.matsgemmeke.battlegrounds.text.TextTemplate;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
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
class ToolsCommandTest {

    private static final String TOOLS_HELP_MENU_TITLE = "tools help menu";
    private static final String SUBCOMMAND_DESCRIPTION = "just a tools command";
    private static final String SUBCOMMAND_USAGE = "/bg tools test <nr>";
    private static final String SUBCOMMAND_SUGGESTION = "/bg tools test ";
    private static final String UNKNOWN_TOOL_MESSAGE = "unknown tool";

    @Mock
    private HelpMenu helpMenu;
    @Mock
    private ShowHitboxesTool showHitboxesTool;
    @Mock
    private Translator translator;
    @InjectMocks
    private ToolsCommand command;

    @Test
    @DisplayName("onDefault sends error message to command sender when args are not empty")
    void onDefault_withArgs() {
        CommandSender commandSender = mock(CommandSender.class);

        when(translator.translate(TranslationKey.TOOL_NOT_EXISTS.getPath())).thenReturn(new TextTemplate(UNKNOWN_TOOL_MESSAGE));

        command.onDefault(commandSender, new String[] { "test" });

        verify(commandSender).sendMessage(UNKNOWN_TOOL_MESSAGE);
    }

    @Test
    @DisplayName("onDefault shows help menu to player as JSON messages")
    void onDefault_playerSender() {
        CommandInfo commandInfo = new CommandInfo(SUBCOMMAND_DESCRIPTION, SUBCOMMAND_USAGE, SUBCOMMAND_SUGGESTION, new String[0]);
        Player player = mock(Player.class);

        when(translator.translate(TranslationKey.TOOLS_HELP_MENU_TITLE.getPath())).thenReturn(new TextTemplate(TOOLS_HELP_MENU_TITLE));

        command.addCommandInfo(commandInfo);
        command.onDefault(player, null);

        verify(helpMenu).sendHelpMenuAsJsonMessages(player, TOOLS_HELP_MENU_TITLE, List.of(commandInfo));
    }

    @Test
    @DisplayName("onDefault shows help menu to sender as normal messages")
    void onDefault_consoleSender() {
        CommandSender sender = mock(CommandSender.class);
        CommandInfo commandInfo = new CommandInfo(SUBCOMMAND_DESCRIPTION, SUBCOMMAND_USAGE, SUBCOMMAND_SUGGESTION, new String[0]);

        when(translator.translate(TranslationKey.TOOLS_HELP_MENU_TITLE.getPath())).thenReturn(new TextTemplate(TOOLS_HELP_MENU_TITLE));

        command.addCommandInfo(commandInfo);
        command.onDefault(sender, null);

        verify(helpMenu).sendHelpMenuAsNormalMessages(sender, TOOLS_HELP_MENU_TITLE, List.of(commandInfo));
    }

    @Test
    @DisplayName("onShowHitboxes executes shot hitboxes command executor")
    void onShowHitboxes() {
        Player player = mock(Player.class);

        command.onShowHitboxes(player, 10, 20.0);

        verify(showHitboxesTool).execute(player, 10, 20.0);
    }
}
