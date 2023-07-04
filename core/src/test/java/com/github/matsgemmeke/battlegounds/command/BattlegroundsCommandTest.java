package com.github.matsgemmeke.battlegounds.command;

import com.github.matsgemmeke.battlegrounds.api.BattleContextProvider;
import com.github.matsgemmeke.battlegrounds.locale.Translator;
import com.github.matsgemmeke.battlegrounds.command.*;
import com.github.matsgemmeke.battlegrounds.game.GameContextFactory;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class BattlegroundsCommandTest {

    private BattleContextProvider contextProvider;
    private Player player;
    private Translator translator;

    @Before
    public void setUp() {
        this.contextProvider = mock(BattleContextProvider.class);
        this.player = mock(Player.class);
        this.translator = mock(Translator.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotBeAbleToGetSubcommandWhenItDoesNotExist() {
        when(translator.translate(anyString())).thenReturn("test");

        GameContextFactory contextFactory = mock(GameContextFactory.class);

        BattlegroundsCommand bgCommand = new BattlegroundsCommand(translator);
        bgCommand.addSubcommand(new CreateGameCommand(contextProvider, contextFactory, translator));

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

        verify(spigot, times(1)).sendMessage(any(BaseComponent.class));
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

        CreateGameCommand command = mock(CreateGameCommand.class);
        when(command.getName()).thenReturn("creategame");

        BattlegroundsCommand bgCommand = new BattlegroundsCommand(translator);
        bgCommand.addSubcommand(command);

        bgCommand.onCreateGame(player, gameId);

        verify(command).execute(player, gameId);
    }

    @Test
    public void shouldBeAbleToRunGiveWeaponCommand() {
        int gameId = 1;
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

        RemoveGameCommand command = mock(RemoveGameCommand.class);
        when(command.getName()).thenReturn("removegame");

        BattlegroundsCommand bgCommand = new BattlegroundsCommand(translator);
        bgCommand.addSubcommand(command);

        bgCommand.onRemoveGame(player, gameId);

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
