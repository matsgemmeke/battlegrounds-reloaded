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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BattlegroundsCommandTest {

    private static final String HELP_MENU_TITLE = "help menu";

    private static final String SUBCOMMAND_NAME = "test";
    private static final String SUBCOMMAND_DESCRIPTION = "just a test";
    private static final String SUBCOMMAND_USAGE = "/bg test <nr>";
    private static final String SUBCOMMAND_SUGGESTION = "/bg test";

    @Mock
    private HelpMenu helpMenu;
    @Mock
    private Player player;
    @Mock
    private Translator translator;
    @InjectMocks
    private BattlegroundsCommand bgCommand;

    @Test
    @DisplayName("onReload throws IllegalArgumentException when the subcommand does not exist")
    void onReload_subcommandNotFound() {
        CommandInfo commandInfo = new CommandInfo("test", null, null, null, null);

        bgCommand.addSubcommand(commandInfo, "test");

        assertThatThrownBy(() -> bgCommand.onReload(player))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unable to find a subcommand by the name reload");
    }

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
        CommandInfo commandInfo = new CommandInfo(SUBCOMMAND_NAME, SUBCOMMAND_DESCRIPTION, SUBCOMMAND_USAGE, SUBCOMMAND_SUGGESTION, new String[0]);
        Player player = mock(Player.class);

        when(translator.translate(TranslationKey.HELP_MENU_TITLE.getPath())).thenReturn(new TextTemplate(HELP_MENU_TITLE));

        bgCommand.addCommandInfo(commandInfo);
        bgCommand.onDefault(player);

        verify(helpMenu).sendHelpMenuAsJsonMessages(player, HELP_MENU_TITLE, List.of(commandInfo));
    }

    @Test
    @DisplayName("onDefault shows help menu to sender as normal messages")
    void onDefault_consoleSender() {
        CommandSender sender = mock(CommandSender.class);
        CommandInfo commandInfo = new CommandInfo(SUBCOMMAND_NAME, SUBCOMMAND_DESCRIPTION, SUBCOMMAND_USAGE, SUBCOMMAND_SUGGESTION, new String[0]);

        when(translator.translate(TranslationKey.HELP_MENU_TITLE.getPath())).thenReturn(new TextTemplate(HELP_MENU_TITLE));

        bgCommand.addCommandInfo(commandInfo);
        bgCommand.onDefault(sender);

        verify(helpMenu).sendHelpMenuAsNormalMessages(sender, HELP_MENU_TITLE, List.of(commandInfo));
    }

    @Test
    @DisplayName("onGivenWeapon executes give weapon command")
    void onGiveWeapon() {
        String[] args = { "test", "weapon" };
        CommandInfo commandInfo = new CommandInfo("giveweapon", null, null, null, null);
        GiveWeaponCommand command = mock(GiveWeaponCommand.class);

        bgCommand.addSubcommand(commandInfo, command);
        bgCommand.onGiveWeapon(player, args);

        verify(command).execute(player, args);
    }

    @Test
    @DisplayName("onReload executes reload command")
    void onReload() {
        CommandInfo commandInfo = new CommandInfo("reload", null, null, null, null);
        ReloadCommand command = mock(ReloadCommand.class);

        bgCommand.addSubcommand(commandInfo, command);
        bgCommand.onReload(player);

        verify(command).execute(player);
    }

    @Test
    @DisplayName("onSetMainLobby executes set main lobby command")
    void onSetMainLobby() {
        CommandInfo commandInfo = new CommandInfo("setmainlobby", null, null, null, null);
        SetMainLobbyCommand command = mock(SetMainLobbyCommand.class);

        bgCommand.addSubcommand(commandInfo, command);
        bgCommand.onSetMainLobby(player);

        verify(command).execute(player);
    }
}
