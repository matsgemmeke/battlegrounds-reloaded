package nl.matsgemmeke.battlegrounds.command;

import net.md_5.bungee.api.chat.BaseComponent;
import nl.matsgemmeke.battlegrounds.text.TextTemplate;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BattlegroundsCommandTest {

    @Mock
    private Player player;
    @Mock
    private Translator translator;
    @InjectMocks
    private BattlegroundsCommand bgCommand;

    @Test
    void onReloadThrowsIllegalArgumentExceptionWhenSubcommandDoesNotExist() {
        CommandSource subcommand = mock(CommandSource.class);
        when(subcommand.getName()).thenReturn("not the reload command");

        bgCommand.addSubcommand(subcommand);

        assertThatThrownBy(() -> bgCommand.onReload(player))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unable to find a subcommand by the name reload");
    }

    @Test
    void onDefaultShowsHelpMenuToPlayerAsJSONMessages() {
        String title = "title";
        ReloadCommand command = mock(ReloadCommand.class);

        Player player = mock(Player.class);
        Player.Spigot spigot = mock(Player.Spigot.class);
        when(player.spigot()).thenReturn(spigot);

        when(translator.translate(TranslationKey.HELP_MENU_TITLE.getPath())).thenReturn(new TextTemplate(title));

        // Add a sample command so the help menu has something to display
        bgCommand.addSubcommand(command);
        bgCommand.onDefault(player);

        verify(player).sendMessage(title);
        verify(spigot).sendMessage(any(BaseComponent.class));
    }

    @Test
    void onDefaultShowsHelpMenuToSenderAsNormalMessages() {
        String title = "title";
        String usage = "usage";

        CommandSender sender = mock(CommandSender.class);

        ReloadCommand command = mock(ReloadCommand.class);
        when(command.getUsage()).thenReturn(usage);

        when(translator.translate(TranslationKey.HELP_MENU_TITLE.getPath())).thenReturn(new TextTemplate(title));

        // Add a sample command so the help menu has something to display
        bgCommand.addSubcommand(command);
        bgCommand.onDefault(sender);

        verify(sender).sendMessage(title);
        verify(sender).sendMessage(usage);
    }

    @Test
    void onCreateSessionExecutesCreateSessionCommand() {
        int gameId = 1;

        CreateSessionCommand command = mock(CreateSessionCommand.class);
        when(command.getName()).thenReturn("createsession");

        bgCommand.addSubcommand(command);
        bgCommand.onCreateSession(player, gameId);

        verify(command).execute(player, gameId);
    }

    @Test
    void onGiveWeaponExecutesGiveWeaponCommand() {
        String[] args = { "test", "weapon" };

        GiveWeaponCommand command = mock(GiveWeaponCommand.class);
        when(command.getName()).thenReturn("giveweapon");

        bgCommand.addSubcommand(command);
        bgCommand.onGiveWeapon(player, args);

        verify(command).execute(player, args);
    }

    @Test
    void onReloadExecutesReloadCommand() {
        ReloadCommand command = mock(ReloadCommand.class);
        when(command.getName()).thenReturn("reload");

        bgCommand.addSubcommand(command);
        bgCommand.onReload(player);

        verify(command).execute(player);
    }

    @Test
    void onRemoveSessionExecutesRemoveSessionCommand() {
        int gameId = 1;

        RemoveSessionCommand command = mock(RemoveSessionCommand.class);
        when(command.getName()).thenReturn("removesession");

        bgCommand.addSubcommand(command);
        bgCommand.onRemoveSession(player, gameId);

        verify(command).execute(player, gameId);
    }

    @Test
    void onSetMainLobbyExecutesSetMainLobbyCommand() {
        SetMainLobbyCommand command = mock(SetMainLobbyCommand.class);
        when(command.getName()).thenReturn("setmainlobby");

        bgCommand.addSubcommand(command);
        bgCommand.onSetMainLobby(player);

        verify(command).execute(player);
    }
}
