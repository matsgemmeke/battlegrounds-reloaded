package nl.matsgemmeke.battlegrounds.command;

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
class BattlegroundsCommandTest {

    private static final String SUBCOMMAND_DESCRIPTION = "just a test";
    private static final String SUBCOMMAND_USAGE = "/bg test <nr>";
    private static final String SUBCOMMAND_SUGGESTION = "/bg test ";

    @Mock
    private GiveWeaponCommandExecutor giveWeaponCommandExecutor;
    @Mock
    private ReloadCommandExecutor reloadCommandExecutor;
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
        Message unknownCommandMessage = mock(Message.class);

        when(translator.translateMessage(TranslationKey.UNKNOWN_COMMAND.getPath())).thenReturn(Optional.of(unknownCommandMessage));

        bgCommand.onCatchUnknown(player);

        verify(unknownCommandMessage).send(player);
    }

    @Test
    @DisplayName("onDefault shows help menu to given console sender")
    void onDefault() {
        CommandInfo commandInfo = new CommandInfo(SUBCOMMAND_DESCRIPTION, SUBCOMMAND_USAGE, SUBCOMMAND_SUGGESTION, new String[0]);
        Message battlegroundsHelpMenuHeaderMessage = mock(Message.class);
        Message battlegroundsHelpMenuFooterMessage = mock(Message.class);

        when(translator.translateMessage(TranslationKey.BATTLEGROUNDS_HELP_MENU_HEADER.getPath())).thenReturn(Optional.of(battlegroundsHelpMenuHeaderMessage));
        when(translator.translateMessage(TranslationKey.BATTLEGROUNDS_HELP_MENU_FOOTER.getPath())).thenReturn(Optional.of(battlegroundsHelpMenuFooterMessage));

        bgCommand.addCommandInfo(commandInfo);
        bgCommand.onDefault(player);

        verify(battlegroundsHelpMenuHeaderMessage).send(player);
        verify(helpMenu).send(player, List.of(commandInfo));
        verify(battlegroundsHelpMenuFooterMessage).send(player);
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
}
