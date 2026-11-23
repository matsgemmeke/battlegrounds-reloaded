package nl.matsgemmeke.battlegrounds.location.command;

import nl.matsgemmeke.battlegrounds.command.CommandInfo;
import nl.matsgemmeke.battlegrounds.command.HelpMenu;
import nl.matsgemmeke.battlegrounds.i18n.Message;
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;
import nl.matsgemmeke.battlegrounds.location.command.executor.RemoveMainLobbyCommandExecutor;
import nl.matsgemmeke.battlegrounds.location.command.executor.SetMainLobbyCommandExecutor;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MainLobbyCommandTest {

    private static final String SUBCOMMAND_DESCRIPTION = "just a main lobby command";
    private static final String SUBCOMMAND_USAGE = "/bg mainlobby test <nr>";
    private static final String SUBCOMMAND_SUGGESTION = "/bg mainlobby test ";

    @Mock
    private HelpMenu helpMenu;
    @Mock
    private Player player;
    @Mock
    private RemoveMainLobbyCommandExecutor removeMainLobbyCommandExecutor;
    @Mock
    private SetMainLobbyCommandExecutor setMainLobbyCommandExecutor;
    @Mock
    private Translator translator;
    @InjectMocks
    private MainLobbyCommand mainLobbyCommand;

    @Test
    @DisplayName("onDefault sends unknown command message when command has args")
    void onDefault_withArgs() {
        String[] args = new String[] { "test" };
        Message unknownCommandMessage = mock(Message.class);

        when(translator.translateMessage(TranslationKey.UNKNOWN_COMMAND.getPath())).thenReturn(Optional.of(unknownCommandMessage));

        mainLobbyCommand.onDefault(player, args);

        verify(unknownCommandMessage).send(player);
    }

    @Test
    @DisplayName("onDefault shows help menu to given console sender")
    void onDefault_noArgs() {
        CommandInfo commandInfo = new CommandInfo(SUBCOMMAND_DESCRIPTION, SUBCOMMAND_USAGE, SUBCOMMAND_SUGGESTION, new String[0]);
        Message mainLobbyHelpMenuHeaderMessage = mock(Message.class);
        Message mainLobbyHelpMenuFooterMessage = mock(Message.class);

        when(translator.translateMessage(TranslationKey.MAIN_LOBBY_HELP_MENU_HEADER.getPath())).thenReturn(Optional.of(mainLobbyHelpMenuHeaderMessage));
        when(translator.translateMessage(TranslationKey.MAIN_LOBBY_HELP_MENU_FOOTER.getPath())).thenReturn(Optional.of(mainLobbyHelpMenuFooterMessage));

        mainLobbyCommand.addCommandInfo(commandInfo);
        mainLobbyCommand.onDefault(player, null);

        verify(mainLobbyHelpMenuHeaderMessage).send(player);
        verify(helpMenu).send(player, List.of(commandInfo));
        verify(mainLobbyHelpMenuFooterMessage).send(player);
    }

    @Test
    @DisplayName("onRemove delegates to RemoveMainLobbyCommandExecutor")
    void onRemove() {
        mainLobbyCommand.onRemove(player);

        verify(removeMainLobbyCommandExecutor).execute(player);
    }

    @Test
    @DisplayName("onSet delegates to SetMainLobbyCommandExecutor")
    void onSet() {
        mainLobbyCommand.onSet(player);

        verify(setMainLobbyCommandExecutor).execute(player);
    }
}
