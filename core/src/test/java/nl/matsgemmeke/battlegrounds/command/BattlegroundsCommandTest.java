package nl.matsgemmeke.battlegrounds.command;

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
class BattlegroundsCommandTest {

    private static final String BATTLEGROUNDS_HELP_MENU_HEADER_TEXT = "help menu header";
    private static final String BATTLEGROUNDS_HELP_MENU_FOOTER_TEXT = "help menu footer";
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

        when(translator.translate(TranslationKey.BATTLEGROUNDS_HELP_MENU_HEADER.getPath())).thenReturn(new TextTemplate(BATTLEGROUNDS_HELP_MENU_HEADER_TEXT));
        when(translator.translate(TranslationKey.BATTLEGROUNDS_HELP_MENU_FOOTER.getPath())).thenReturn(new TextTemplate(BATTLEGROUNDS_HELP_MENU_FOOTER_TEXT));

        bgCommand.addCommandInfo(commandInfo);
        bgCommand.onDefault(player);

        verify(player).sendMessage(BATTLEGROUNDS_HELP_MENU_HEADER_TEXT);
        verify(helpMenu).sendHelpMenuAsJsonMessages(player, List.of(commandInfo));
        verify(player).sendMessage(BATTLEGROUNDS_HELP_MENU_FOOTER_TEXT);
    }

    @Test
    @DisplayName("onDefault shows help menu to sender as normal messages")
    void onDefault_consoleSender() {
        CommandSender sender = mock(CommandSender.class);
        CommandInfo commandInfo = new CommandInfo(SUBCOMMAND_DESCRIPTION, SUBCOMMAND_USAGE, SUBCOMMAND_SUGGESTION, new String[0]);

        when(translator.translate(TranslationKey.BATTLEGROUNDS_HELP_MENU_HEADER.getPath())).thenReturn(new TextTemplate(BATTLEGROUNDS_HELP_MENU_HEADER_TEXT));
        when(translator.translate(TranslationKey.BATTLEGROUNDS_HELP_MENU_FOOTER.getPath())).thenReturn(new TextTemplate(BATTLEGROUNDS_HELP_MENU_FOOTER_TEXT));

        bgCommand.addCommandInfo(commandInfo);
        bgCommand.onDefault(sender);

        verify(sender).sendMessage(BATTLEGROUNDS_HELP_MENU_HEADER_TEXT);
        verify(helpMenu).sendHelpMenuAsNormalMessages(sender, List.of(commandInfo));
        verify(sender).sendMessage(BATTLEGROUNDS_HELP_MENU_FOOTER_TEXT);
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
