package nl.matsgemmeke.battlegrounds.arena.command;

import nl.matsgemmeke.battlegrounds.arena.command.executor.lobby.RemoveLobbyCommandExecutor;
import nl.matsgemmeke.battlegrounds.arena.command.executor.lobby.SetLobbyCommandExecutor;
import nl.matsgemmeke.battlegrounds.command.CommandInfo;
import nl.matsgemmeke.battlegrounds.command.HelpMenu;
import nl.matsgemmeke.battlegrounds.i18n.Message;
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LobbyCommandTest {

    private static final int ARENA_ID = 1;
    private static final String SUBCOMMAND_DESCRIPTION = "just a lobby command";
    private static final String SUBCOMMAND_USAGE = "/bg arena lobby test <nr>";
    private static final String SUBCOMMAND_SUGGESTION = "/bg arena lobby test ";
    private static final String[] SUBCOMMAND_PERMISSIONS = new String[0];

    @Mock
    private RemoveLobbyCommandExecutor removeLobbyCommandExecutor;
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
        Message unknownCommandMessage = mock(Message.class);

        when(translator.translateMessage(TranslationKey.UNKNOWN_COMMAND.getPath())).thenReturn(Optional.of(unknownCommandMessage));

        command.onDefault(player, args);

        verify(unknownCommandMessage).send(player);
    }

    @Test
    @DisplayName("onDefault shows help menu to given console sender")
    void onDefault_noArgs() {
        CommandInfo commandInfo = new CommandInfo(SUBCOMMAND_DESCRIPTION, SUBCOMMAND_USAGE, SUBCOMMAND_SUGGESTION, SUBCOMMAND_PERMISSIONS);
        Message lobbyHelpMenuHeaderMessage = mock(Message.class);
        Message lobbyHelpMenuFooterMessage = mock(Message.class);

        when(translator.translateMessage(TranslationKey.LOBBY_HELP_MENU_HEADER.getPath())).thenReturn(Optional.of(lobbyHelpMenuHeaderMessage));
        when(translator.translateMessage(TranslationKey.LOBBY_HELP_MENU_FOOTER.getPath())).thenReturn(Optional.of(lobbyHelpMenuFooterMessage));

        command.addCommandInfo(commandInfo);
        command.onDefault(player, null);

        verify(lobbyHelpMenuHeaderMessage).send(player);
        verify(helpMenu).send(player, List.of(commandInfo));
        verify(lobbyHelpMenuFooterMessage).send(player);
    }

    @Test
    @DisplayName("onRemove delegates to remove lobby executor")
    void onRemove() {
        command.onRemove(player, ARENA_ID);

        verify(removeLobbyCommandExecutor).execute(player, ARENA_ID);
    }

    @Test
    @DisplayName("onSet delegates to set lobby executor")
    void onSet() {
        command.onSet(player, ARENA_ID);

        verify(setLobbyCommandExecutor).execute(player, ARENA_ID);
    }
}
