package nl.matsgemmeke.battlegrounds.command.map;

import nl.matsgemmeke.battlegrounds.command.CommandInfo;
import nl.matsgemmeke.battlegrounds.command.HelpMenu;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MapCommandTest {

    private static final int ARENA_ID = 1;
    private static final String MAP_NAME = "The Map";

    private static final String MAP_HELP_MENU_TITLE = "map help menu";
    private static final String SUBCOMMAND_DESCRIPTION = "just a map command";
    private static final String SUBCOMMAND_USAGE = "/bg arena map test <nr>";
    private static final String SUBCOMMAND_SUGGESTION = "/bg arena map test ";
    private static final String[] SUBCOMMAND_PERMISSIONS = new String[0];
    private static final String UNKNOWN_COMMAND_MESSAGE = "unknown command";

    @Mock
    private CommandSender sender;
    @Mock
    private CreateMapCommandExecutor createMapCommandExecutor;
    @Mock
    private HelpMenu helpMenu;
    @Mock
    private Translator translator;
    @InjectMocks
    private MapCommand command;

    @Test
    @DisplayName("onDefault sends unknown command message when command has args")
    void onDefault_withArgs() {
        String[] args = new String[] { "test" };

        when(translator.translate(TranslationKey.UNKNOWN_COMMAND.getPath())).thenReturn(new TextTemplate(UNKNOWN_COMMAND_MESSAGE));

        command.onDefault(sender, args);

        verify(sender).sendMessage(UNKNOWN_COMMAND_MESSAGE);
    }

    @Test
    @DisplayName("onDefault shows help menu to player as JSON messages")
    void onDefault_playerSender() {
        CommandInfo commandInfo = new CommandInfo(SUBCOMMAND_DESCRIPTION, SUBCOMMAND_USAGE, SUBCOMMAND_SUGGESTION, SUBCOMMAND_PERMISSIONS);
        Player player = mock(Player.class);

        when(translator.translate(TranslationKey.MAP_HELP_MENU_TITLE.getPath())).thenReturn(new TextTemplate(MAP_HELP_MENU_TITLE));

        command.addCommandInfo(commandInfo);
        command.onDefault(player, null);

        verify(helpMenu).sendHelpMenuAsJsonMessages(player, MAP_HELP_MENU_TITLE, List.of(commandInfo));
    }

    @Test
    @DisplayName("onDefault shows help menu to sender as normal messages")
    void onDefault_consoleSender() {
        CommandInfo commandInfo = new CommandInfo(SUBCOMMAND_DESCRIPTION, SUBCOMMAND_USAGE, SUBCOMMAND_SUGGESTION, SUBCOMMAND_PERMISSIONS);

        when(translator.translate(TranslationKey.MAP_HELP_MENU_TITLE.getPath())).thenReturn(new TextTemplate(MAP_HELP_MENU_TITLE));

        command.addCommandInfo(commandInfo);
        command.onDefault(sender, null);

        verify(helpMenu).sendHelpMenuAsNormalMessages(sender, MAP_HELP_MENU_TITLE, List.of(commandInfo));
    }

    @Test
    @DisplayName("onCreate delegates to create map executor")
    void onCreate() {
        command.onCreate(sender, ARENA_ID, MAP_NAME);

        verify(createMapCommandExecutor).execute(sender, ARENA_ID, MAP_NAME);
    }
}
