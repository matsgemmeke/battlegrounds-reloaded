package nl.matsgemmeke.battlegrounds.command.arena;

import nl.matsgemmeke.battlegrounds.command.CommandInfo;
import nl.matsgemmeke.battlegrounds.command.HelpMenu;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArenaCommandTest {

    private static final int ARENA_ID = 1;
    private static final String ARENA_HELP_MENU_TITLE = "arena help menu";
    private static final String SUBCOMMAND_DESCRIPTION = "just an arena command";
    private static final String SUBCOMMAND_USAGE = "/bg arena test <nr>";
    private static final String SUBCOMMAND_SUGGESTION = "/bg arena test ";

    @Mock
    private CreateArenaExecutor createArenaExecutor;
    @Mock
    private RemoveArenaExecutor removeArenaExecutor;
    @Mock
    private HelpMenu helpMenu;
    @Mock
    private Translator translator;
    @InjectMocks
    private ArenaCommand command;

    @Test
    @DisplayName("onDefault shows help menu to player as JSON messages")
    void onDefault_playerSender() {
        CommandInfo commandInfo = new CommandInfo(SUBCOMMAND_DESCRIPTION, SUBCOMMAND_USAGE, SUBCOMMAND_SUGGESTION, new String[0]);
        Player player = mock(Player.class);

        when(translator.translate(TranslationKey.ARENA_HELP_MENU_TITLE.getPath())).thenReturn(new TextTemplate(ARENA_HELP_MENU_TITLE));

        command.addCommandInfo(commandInfo);
        command.onDefault(player);

        verify(helpMenu).sendHelpMenuAsJsonMessages(player, ARENA_HELP_MENU_TITLE, List.of(commandInfo));
    }

    @Test
    @DisplayName("onDefault shows help menu to sender as normal messages")
    void onDefault_consoleSender() {
        CommandSender sender = mock(CommandSender.class);
        CommandInfo commandInfo = new CommandInfo(SUBCOMMAND_DESCRIPTION, SUBCOMMAND_USAGE, SUBCOMMAND_SUGGESTION, new String[0]);

        when(translator.translate(TranslationKey.ARENA_HELP_MENU_TITLE.getPath())).thenReturn(new TextTemplate(ARENA_HELP_MENU_TITLE));

        command.addCommandInfo(commandInfo);
        command.onDefault(sender);

        verify(helpMenu).sendHelpMenuAsNormalMessages(sender, ARENA_HELP_MENU_TITLE, List.of(commandInfo));
    }

    @Test
    @DisplayName("onCreate delegates to create arena executor")
    void onCreate() {
        CommandSender sender = mock(CommandSender.class);

        command.onCreate(sender, ARENA_ID);

        verify(createArenaExecutor).execute(sender, ARENA_ID);
    }

    @Test
    @DisplayName("onRemove delegates to create arena executor")
    void onRemove() {
        CommandSender sender = mock(CommandSender.class);

        command.onRemove(sender, ARENA_ID);

        verify(removeArenaExecutor).execute(sender, ARENA_ID);
    }
}
