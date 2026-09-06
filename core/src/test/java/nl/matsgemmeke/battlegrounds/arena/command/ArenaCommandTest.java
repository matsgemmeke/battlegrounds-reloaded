package nl.matsgemmeke.battlegrounds.arena.command;

import nl.matsgemmeke.battlegrounds.arena.command.executor.CreateArenaCommandExecutor;
import nl.matsgemmeke.battlegrounds.arena.command.executor.RemoveArenaCommandExecutor;
import nl.matsgemmeke.battlegrounds.command.CommandInfo;
import nl.matsgemmeke.battlegrounds.command.HelpMenu;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArenaCommandTest {

    private static final int ARENA_ID = 1;
    private static final String ARENA_HELP_MENU_HEADER_TEXT = "arena help menu header";
    private static final String ARENA_HELP_MENU_FOOTER_TEXT = "arena help menu footer";
    private static final String SUBCOMMAND_DESCRIPTION = "just an arena command";
    private static final String SUBCOMMAND_USAGE = "/bg arena test <nr>";
    private static final String SUBCOMMAND_SUGGESTION = "/bg arena test ";
    private static final String UNKNOWN_COMMAND_MESSAGE = "unknown command";

    @Mock
    private CreateArenaCommandExecutor createArenaCommandExecutor;
    @Mock
    private RemoveArenaCommandExecutor removeArenaCommandExecutor;
    @Mock
    private HelpMenu helpMenu;
    @Mock
    private Player player;
    @Mock
    private Translator translator;
    @InjectMocks
    private ArenaCommand command;

    @Test
    @DisplayName("onDefault sends unknown command message when command has args")
    void onDefault_withArgs() {
        String[] args = new String[] { "test" };

        when(translator.translate(TranslationKey.UNKNOWN_COMMAND.getPath())).thenReturn(new TextTemplate(UNKNOWN_COMMAND_MESSAGE));

        command.onDefault(player, args);

        verify(player).sendMessage(UNKNOWN_COMMAND_MESSAGE);
    }

    @Test
    @DisplayName("onDefault shows help menu to player as JSON messages")
    void onDefault_playerSender() {
        CommandInfo commandInfo = new CommandInfo(SUBCOMMAND_DESCRIPTION, SUBCOMMAND_USAGE, SUBCOMMAND_SUGGESTION, new String[0]);

        when(translator.translate(TranslationKey.ARENA_HELP_MENU_HEADER.getPath())).thenReturn(new TextTemplate(ARENA_HELP_MENU_HEADER_TEXT));
        when(translator.translate(TranslationKey.ARENA_HELP_MENU_FOOTER.getPath())).thenReturn(new TextTemplate(ARENA_HELP_MENU_FOOTER_TEXT));

        command.addCommandInfo(commandInfo);
        command.onDefault(player, null);

        verify(player).sendMessage(ARENA_HELP_MENU_HEADER_TEXT);
        verify(helpMenu).sendHelpMenuAsJsonMessages(player, List.of(commandInfo));
        verify(player).sendMessage(ARENA_HELP_MENU_FOOTER_TEXT);
    }

    @Test
    @DisplayName("onDefault shows help menu to sender as normal messages")
    void onDefault_consoleSender() {
        CommandInfo commandInfo = new CommandInfo(SUBCOMMAND_DESCRIPTION, SUBCOMMAND_USAGE, SUBCOMMAND_SUGGESTION, new String[0]);
        CommandSender sender = mock(CommandSender.class);

        when(translator.translate(TranslationKey.ARENA_HELP_MENU_HEADER.getPath())).thenReturn(new TextTemplate(ARENA_HELP_MENU_HEADER_TEXT));
        when(translator.translate(TranslationKey.ARENA_HELP_MENU_FOOTER.getPath())).thenReturn(new TextTemplate(ARENA_HELP_MENU_FOOTER_TEXT));

        command.addCommandInfo(commandInfo);
        command.onDefault(sender, null);

        verify(sender).sendMessage(ARENA_HELP_MENU_HEADER_TEXT);
        verify(helpMenu).sendHelpMenuAsNormalMessages(sender, List.of(commandInfo));
        verify(sender).sendMessage(ARENA_HELP_MENU_FOOTER_TEXT);
    }

    @Test
    @DisplayName("onCreate delegates to create arena executor")
    void onCreate() {
        command.onCreate(player, ARENA_ID);

        verify(createArenaCommandExecutor).execute(player, ARENA_ID);
    }

    @Test
    @DisplayName("onRemove delegates to create arena executor")
    void onRemove() {
        command.onRemove(player, ARENA_ID);

        verify(removeArenaCommandExecutor).execute(player, ARENA_ID);
    }
}
