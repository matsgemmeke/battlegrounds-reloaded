package nl.matsgemmeke.battlegrounds.location.command;

import nl.matsgemmeke.battlegrounds.command.CommandInfo;
import nl.matsgemmeke.battlegrounds.command.HelpMenu;
import nl.matsgemmeke.battlegrounds.fixture.LanguageFixture;
import nl.matsgemmeke.battlegrounds.i18n.TextTemplate;
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;
import nl.matsgemmeke.battlegrounds.location.command.executor.RemoveMainLobbyCommandExecutor;
import nl.matsgemmeke.battlegrounds.location.command.executor.SetMainLobbyCommandExecutor;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MainLobbyCommandTest {

    private static final String SUBCOMMAND_DESCRIPTION = "just a main lobby command";
    private static final String SUBCOMMAND_USAGE = "/bg mainlobby test <nr>";
    private static final String SUBCOMMAND_SUGGESTION = "/bg mainlobby test ";

    private static final TextTemplate MAIN_LOBBY_HELP_MENU_HEADER_TEXT_TEMPLATE = LanguageFixture.getTextTemplate(TranslationKey.MAIN_LOBBY_HELP_MENU_HEADER.getPath());
    private static final TextTemplate MAIN_LOBBY_HELP_MENU_FOOTER_TEXT_TEMPLATE = LanguageFixture.getTextTemplate(TranslationKey.MAIN_LOBBY_HELP_MENU_FOOTER.getPath());
    private static final TextTemplate UNKNOWN_COMMAND_TEXT_TEMPLATE = LanguageFixture.getTextTemplate(TranslationKey.UNKNOWN_COMMAND.getPath());

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

        when(translator.translate(TranslationKey.UNKNOWN_COMMAND.getPath())).thenReturn(UNKNOWN_COMMAND_TEXT_TEMPLATE);

        mainLobbyCommand.onDefault(player, args);

        verify(player).sendMessage("&cUnknown command, please type /bg help");
    }

    @Test
    @DisplayName("onDefault shows help menu to player as JSON messages")
    void onDefault_playerSender() {
        CommandInfo commandInfo = new CommandInfo(SUBCOMMAND_DESCRIPTION, SUBCOMMAND_USAGE, SUBCOMMAND_SUGGESTION, new String[0]);

        when(translator.translate(TranslationKey.MAIN_LOBBY_HELP_MENU_HEADER.getPath())).thenReturn(MAIN_LOBBY_HELP_MENU_HEADER_TEXT_TEMPLATE);
        when(translator.translate(TranslationKey.MAIN_LOBBY_HELP_MENU_FOOTER.getPath())).thenReturn(MAIN_LOBBY_HELP_MENU_FOOTER_TEXT_TEMPLATE);

        mainLobbyCommand.addCommandInfo(commandInfo);
        mainLobbyCommand.onDefault(player, null);

        verify(player).sendMessage(" \n&6&l Main lobby commands\n ");
        verify(helpMenu).sendHelpMenuAsJsonMessages(player, List.of(commandInfo));
        verify(player).sendMessage(" ");
    }

    @Test
    @DisplayName("onDefault shows help menu to sender as normal messages")
    void onDefault_consoleSender() {
        CommandInfo commandInfo = new CommandInfo(SUBCOMMAND_DESCRIPTION, SUBCOMMAND_USAGE, SUBCOMMAND_SUGGESTION, new String[0]);
        CommandSender sender = mock(CommandSender.class);

        when(translator.translate(TranslationKey.MAIN_LOBBY_HELP_MENU_HEADER.getPath())).thenReturn(MAIN_LOBBY_HELP_MENU_HEADER_TEXT_TEMPLATE);
        when(translator.translate(TranslationKey.MAIN_LOBBY_HELP_MENU_FOOTER.getPath())).thenReturn(MAIN_LOBBY_HELP_MENU_FOOTER_TEXT_TEMPLATE);

        mainLobbyCommand.addCommandInfo(commandInfo);
        mainLobbyCommand.onDefault(sender, null);

        verify(sender).sendMessage(" \n&6&l Main lobby commands\n ");
        verify(helpMenu).sendHelpMenuAsNormalMessages(sender, List.of(commandInfo));
        verify(sender).sendMessage(" ");
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
