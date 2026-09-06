package nl.matsgemmeke.battlegrounds.arena.command;

import nl.matsgemmeke.battlegrounds.arena.command.executor.lobby.SetLobbyCommandExecutor;
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
class LobbyCommandTest {

    private static final int ARENA_ID = 1;
    private static final String LOBBY_HELP_MENU_HEADER_MESSAGE = "lobby help menu header";
    private static final String UNKNOWN_COMMAND_MESSAGE = "unknown command";

    private static final String SUBCOMMAND_DESCRIPTION = "just a lobby command";
    private static final String SUBCOMMAND_USAGE = "/bg arena lobby test <nr>";
    private static final String SUBCOMMAND_SUGGESTION = "/bg arena lobby test ";
    private static final String[] SUBCOMMAND_PERMISSIONS = new String[0];

    @Mock
    private SetLobbyCommandExecutor setLobbyCommandExecutor;
    @Mock
    private HelpMenu helpMenu;
    @Mock
    private Translator translator;
    @Mock
    private Player player;
    @InjectMocks
    private LobbyCommand command;

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

        when(translator.translate(TranslationKey.LOBBY_HELP_MENU_HEADER.getPath())).thenReturn(new TextTemplate(LOBBY_HELP_MENU_HEADER_MESSAGE));

        command.addCommandInfo(commandInfo);
        command.onDefault(player, null);

        verify(helpMenu).sendHelpMenuAsJsonMessages(player, LOBBY_HELP_MENU_HEADER_MESSAGE, List.of(commandInfo));
    }

    @Test
    @DisplayName("onDefault shows help menu to sender as normal messages")
    void onDefault_consoleSender() {
        CommandInfo commandInfo = new CommandInfo(SUBCOMMAND_DESCRIPTION, SUBCOMMAND_USAGE, SUBCOMMAND_SUGGESTION, SUBCOMMAND_PERMISSIONS);
        CommandSender sender = mock(CommandSender.class);

        when(translator.translate(TranslationKey.LOBBY_HELP_MENU_HEADER.getPath())).thenReturn(new TextTemplate(LOBBY_HELP_MENU_HEADER_MESSAGE));

        command.addCommandInfo(commandInfo);
        command.onDefault(sender, null);

        verify(helpMenu).sendHelpMenuAsNormalMessages(sender, LOBBY_HELP_MENU_HEADER_MESSAGE, List.of(commandInfo));
    }

    @Test
    @DisplayName("onSet delegates to set lobby executor")
    void onSet() {
        command.onSet(player, ARENA_ID);

        verify(setLobbyCommandExecutor).execute(player, ARENA_ID);
    }
}
