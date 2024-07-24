package nl.matsgemmeke.battlegrounds.command;

import net.md_5.bungee.api.chat.BaseComponent;
import nl.matsgemmeke.battlegrounds.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.session.SessionFactory;
import nl.matsgemmeke.battlegrounds.locale.Translator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class BattlegroundsCommandTest {

    private Player player;
    private Translator translator;

    @Before
    public void setUp() {
        player = mock(Player.class);
        translator = mock(Translator.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotBeAbleToGetSubcommandWhenItDoesNotExist() {
        when(translator.translate(anyString())).thenReturn("test");

        GameContextProvider contextProvider = mock(GameContextProvider.class);
        SessionFactory sessionFactory = mock(SessionFactory.class);

        BattlegroundsCommand bgCommand = new BattlegroundsCommand(translator);
        bgCommand.addSubcommand(new CreateSessionCommand(contextProvider, sessionFactory, translator));

        bgCommand.onReload(player);
    }

    @Test
    public void shouldShowHelpMenuToPlayerAsJSONMessages() {
        Player player = mock(Player.class);
        Player.Spigot spigot = mock(Player.Spigot.class);
        when(player.spigot()).thenReturn(spigot);

        ReloadCommand command = mock(ReloadCommand.class);
        when(command.getName()).thenReturn("reload");

        BattlegroundsCommand bgCommand = new BattlegroundsCommand(translator);
        // Add a sample command so the help menu has something to display
        bgCommand.addSubcommand(command);

        bgCommand.onDefault(player);

        verify(spigot).sendMessage(any(BaseComponent.class));
    }

    @Test
    public void shouldShowHelpMenuToSenderAsNormalMessages() {
        CommandSender sender = mock(CommandSender.class);

        ReloadCommand command = mock(ReloadCommand.class);
        when(command.getName()).thenReturn("reload");

        BattlegroundsCommand bgCommand = new BattlegroundsCommand(translator);
        // Add a sample command so the help menu has something to display
        bgCommand.addSubcommand(command);

        bgCommand.onDefault(sender);

        verify(sender, atLeast(1)).sendMessage(anyString());
    }

    @Test
    public void shouldBeAbleToRunCreateGameCommand() {
        int gameId = 1;

        CreateSessionCommand command = mock(CreateSessionCommand.class);
        when(command.getName()).thenReturn("createsession");

        BattlegroundsCommand bgCommand = new BattlegroundsCommand(translator);
        bgCommand.addSubcommand(command);

        bgCommand.onCreateSession(player, gameId);

        verify(command).execute(player, gameId);
    }

    @Test
    public void shouldBeAbleToRunGiveWeaponCommand() {
        String weapon = "test";

        GiveWeaponCommand command = mock(GiveWeaponCommand.class);
        when(command.getName()).thenReturn("giveweapon");

        BattlegroundsCommand bgCommand = new BattlegroundsCommand(translator);
        bgCommand.addSubcommand(command);

        bgCommand.onGiveWeapon(player, weapon);

        verify(command).execute(player, weapon);
    }

    @Test
    public void shouldBeAbleToRunReloadCommand() {
        ReloadCommand command = mock(ReloadCommand.class);
        when(command.getName()).thenReturn("reload");

        BattlegroundsCommand bgCommand = new BattlegroundsCommand(translator);
        bgCommand.addSubcommand(command);

        bgCommand.onReload(player);

        verify(command).execute(player);
    }

    @Test
    public void shouldBeAbleToRunRemoveGameCommand() {
        int gameId = 1;

        RemoveSessionCommand command = mock(RemoveSessionCommand.class);
        when(command.getName()).thenReturn("removesession");

        BattlegroundsCommand bgCommand = new BattlegroundsCommand(translator);
        bgCommand.addSubcommand(command);

        bgCommand.onRemoveSession(player, gameId);

        verify(command).execute(player, gameId);
    }

    @Test
    public void shouldBeAbleToRunSetMainLobbyCommand() {
        SetMainLobbyCommand command = mock(SetMainLobbyCommand.class);
        when(command.getName()).thenReturn("setmainlobby");

        BattlegroundsCommand bgCommand = new BattlegroundsCommand(translator);
        bgCommand.addSubcommand(command);

        bgCommand.onSetMainLobby(player);

        verify(command).execute(player);
    }
}
