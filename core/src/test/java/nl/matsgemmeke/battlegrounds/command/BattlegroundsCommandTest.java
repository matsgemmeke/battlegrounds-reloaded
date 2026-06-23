package nl.matsgemmeke.battlegrounds.command;

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
class BattlegroundsCommandTest {

    private static final String BATTLEGROUNDS_HELP_MENU_TITLE = "help menu";
    private static final String SUBCOMMAND_DESCRIPTION = "just a test";
    private static final String SUBCOMMAND_USAGE = "/bg test <nr>";
    private static final String SUBCOMMAND_SUGGESTION = "/bg test ";

    @Mock
    private GiveWeaponCommandExecutor giveWeaponCommandExecutor;
    @Mock
    private ReloadCommandExecutor reloadCommandExecutor;
    @Mock
    private SetMainLobbyCommandExecutor setMainLobbyCommandExecutor;
    @Mock
    private HelpMenu helpMenu;
    @Mock
    private Player player;
    @Mock
    private Translator translator;
    @InjectMocks
    private BattlegroundsCommand bgCommand;

    @Test
    @DisplayName("onCatchUnknown sends error message to command sender")
    void onCatchUnknown() {
        when(translator.translate(TranslationKey.UNKNOWN_COMMAND.getPath())).thenReturn(new TextTemplate("test"));

        bgCommand.onCatchUnknown(player);

        verify(player).sendMessage("test");
    }

    @Test
    @DisplayName("onDefault shows help menu to player as JSON messages")
    void onDefault_playerSender() {
        CommandInfo commandInfo = new CommandInfo(SUBCOMMAND_DESCRIPTION, SUBCOMMAND_USAGE, SUBCOMMAND_SUGGESTION, new String[0]);

        when(translator.translate(TranslationKey.BATTLEGROUNDS_HELP_MENU_TITLE.getPath())).thenReturn(new TextTemplate(BATTLEGROUNDS_HELP_MENU_TITLE));

        bgCommand.addCommandInfo(commandInfo);
        bgCommand.onDefault(player);

        verify(helpMenu).sendHelpMenuAsJsonMessages(player, BATTLEGROUNDS_HELP_MENU_TITLE, List.of(commandInfo));
    }

    @Test
    @DisplayName("onDefault shows help menu to sender as normal messages")
    void onDefault_consoleSender() {
        CommandSender sender = mock(CommandSender.class);
        CommandInfo commandInfo = new CommandInfo(SUBCOMMAND_DESCRIPTION, SUBCOMMAND_USAGE, SUBCOMMAND_SUGGESTION, new String[0]);

        when(translator.translate(TranslationKey.BATTLEGROUNDS_HELP_MENU_TITLE.getPath())).thenReturn(new TextTemplate(BATTLEGROUNDS_HELP_MENU_TITLE));

        bgCommand.addCommandInfo(commandInfo);
        bgCommand.onDefault(sender);

        verify(helpMenu).sendHelpMenuAsNormalMessages(sender, BATTLEGROUNDS_HELP_MENU_TITLE, List.of(commandInfo));
    }

    @Test
    @DisplayName("onGiveWeapon delegates to GiveWeaponExecutor")
    void onGiveWeapon() {
        String[] args = { "test", "weapon" };

        bgCommand.onGiveWeapon(player, args);

        verify(giveWeaponCommandExecutor).execute(player, args);
    }

    @Test
    @DisplayName("onReload delegates to ReloadCommandExecutor")
    void onReload() {
        bgCommand.onReload(player);

        verify(reloadCommandExecutor).execute(player);
    }

    @Test
    @DisplayName("onSetMainLobby delegates to SetMainLobbyCommandExecutor")
    void onSetMainLobby() {
        bgCommand.onSetMainLobby(player);

        verify(setMainLobbyCommandExecutor).execute(player);
    }
}
