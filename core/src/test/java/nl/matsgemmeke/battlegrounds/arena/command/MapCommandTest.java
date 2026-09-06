package nl.matsgemmeke.battlegrounds.arena.command;

import nl.matsgemmeke.battlegrounds.arena.command.executor.CreateMapCommandExecutor;
import nl.matsgemmeke.battlegrounds.arena.command.executor.RemoveMapCommandExecutor;
import nl.matsgemmeke.battlegrounds.arena.command.executor.SelectMapCommandExecutor;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MapCommandTest {

    private static final int ARENA_ID = 1;
    private static final String MAP_NAME = "The Map";

    private static final String MAP_HELP_MENU_HEADER_TEXT = "map help menu header";
    private static final String MAP_HELP_MENU_FOOTER_TEXT = "map help menu footer";
    private static final String SUBCOMMAND_DESCRIPTION = "just a map command";
    private static final String SUBCOMMAND_USAGE = "/bg arena map test <nr>";
    private static final String SUBCOMMAND_SUGGESTION = "/bg arena map test ";
    private static final String[] SUBCOMMAND_PERMISSIONS = new String[0];
    private static final String UNKNOWN_COMMAND_MESSAGE = "unknown command";

    @Mock
    private CreateMapCommandExecutor createMapCommandExecutor;
    @Mock
    private RemoveMapCommandExecutor removeMapCommandExecutor;
    @Mock
    private SelectMapCommandExecutor selectMapCommandExecutor;
    @Mock
    private HelpMenu helpMenu;
    @Mock
    private Player player;
    @Mock
    private Translator translator;
    @InjectMocks
    private MapCommand command;

    @Test
    @DisplayName("onDefault sends unknown command message when command has args")
    void onDefault_withArgs() {
        String[] args = new String[] { "test" };

        when(translator.translate(TranslationKey.UNKNOWN_COMMAND.getPath())).thenReturn(new TextTemplate(UNKNOWN_COMMAND_MESSAGE));

        command.onDefault(player, args);

        verify(player).sendMessage(UNKNOWN_COMMAND_MESSAGE);
    }

    @Test
    @DisplayName("onDefault shows help menu to player as JSON messages")
    void onDefault_playerSender() {
        CommandInfo commandInfo = new CommandInfo(SUBCOMMAND_DESCRIPTION, SUBCOMMAND_USAGE, SUBCOMMAND_SUGGESTION, SUBCOMMAND_PERMISSIONS);

        when(translator.translate(TranslationKey.MAP_HELP_MENU_HEADER.getPath())).thenReturn(new TextTemplate(MAP_HELP_MENU_HEADER_TEXT));
        when(translator.translate(TranslationKey.MAP_HELP_MENU_FOOTER.getPath())).thenReturn(new TextTemplate(MAP_HELP_MENU_FOOTER_TEXT));

        command.addCommandInfo(commandInfo);
        command.onDefault(player, null);

        verify(player).sendMessage(MAP_HELP_MENU_HEADER_TEXT);
        verify(helpMenu).sendHelpMenuAsJsonMessages(player, List.of(commandInfo));
        verify(player).sendMessage(MAP_HELP_MENU_FOOTER_TEXT);
    }

    @Test
    @DisplayName("onDefault shows help menu to sender as normal messages")
    void onDefault_consoleSender() {
        CommandInfo commandInfo = new CommandInfo(SUBCOMMAND_DESCRIPTION, SUBCOMMAND_USAGE, SUBCOMMAND_SUGGESTION, SUBCOMMAND_PERMISSIONS);
        CommandSender sender = mock(CommandSender.class);

        when(translator.translate(TranslationKey.MAP_HELP_MENU_HEADER.getPath())).thenReturn(new TextTemplate(MAP_HELP_MENU_HEADER_TEXT));
        when(translator.translate(TranslationKey.MAP_HELP_MENU_FOOTER.getPath())).thenReturn(new TextTemplate(MAP_HELP_MENU_FOOTER_TEXT));

        command.addCommandInfo(commandInfo);
        command.onDefault(sender, null);

        verify(sender).sendMessage(MAP_HELP_MENU_HEADER_TEXT);
        verify(helpMenu).sendHelpMenuAsNormalMessages(sender, List.of(commandInfo));
        verify(sender).sendMessage(MAP_HELP_MENU_FOOTER_TEXT);
    }

    @Test
    @DisplayName("onCreate delegates to create map executor")
    void onCreate() {
        command.onCreate(player, ARENA_ID, MAP_NAME);

        verify(createMapCommandExecutor).execute(player, ARENA_ID, MAP_NAME);
    }

    @Test
    @DisplayName("onRemove delegates to remove map executor")
    void onRemove() {
        command.onRemove(player, ARENA_ID, MAP_NAME);

        verify(removeMapCommandExecutor).execute(player, ARENA_ID, MAP_NAME);
    }

    @Test
    @DisplayName("onSelect delegates to SelectMapCommandExecutor")
    void onSelect() {
        command.onSelect(player, ARENA_ID, MAP_NAME);

        verify(selectMapCommandExecutor).execute(player, ARENA_ID, MAP_NAME);
    }
}
