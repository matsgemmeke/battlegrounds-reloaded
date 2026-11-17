package nl.matsgemmeke.battlegrounds.arena.command;

import nl.matsgemmeke.battlegrounds.arena.command.executor.CreateArenaCommandExecutor;
import nl.matsgemmeke.battlegrounds.arena.command.executor.RemoveArenaCommandExecutor;
import nl.matsgemmeke.battlegrounds.command.CommandInfo;
import nl.matsgemmeke.battlegrounds.command.HelpMenu;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArenaCommandTest {

    private static final int ARENA_ID = 1;
    private static final String SUBCOMMAND_DESCRIPTION = "just an arena command";
    private static final String SUBCOMMAND_USAGE = "/bg arena test <nr>";
    private static final String SUBCOMMAND_SUGGESTION = "/bg arena test ";

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
        Message unknownCommandMessage = mock(Message.class);

        when(translator.translateMessage(TranslationKey.UNKNOWN_COMMAND.getPath())).thenReturn(Optional.of(unknownCommandMessage));

        command.onDefault(player, args);

        verify(unknownCommandMessage).send(player);
    }

    @Test
    @DisplayName("onDefault shows help menu to given console sender")
    void onDefault_noArgs() {
        CommandInfo commandInfo = new CommandInfo(SUBCOMMAND_DESCRIPTION, SUBCOMMAND_USAGE, SUBCOMMAND_SUGGESTION, new String[0]);
        Message arenaHelpMenuHeaderMessage = mock(Message.class);
        Message arenaHelpMenuFooterMessage = mock(Message.class);

        when(translator.translateMessage(TranslationKey.ARENA_HELP_MENU_HEADER.getPath())).thenReturn(Optional.of(arenaHelpMenuHeaderMessage));
        when(translator.translateMessage(TranslationKey.ARENA_HELP_MENU_FOOTER.getPath())).thenReturn(Optional.of(arenaHelpMenuFooterMessage));

        command.addCommandInfo(commandInfo);
        command.onDefault(player, null);

        verify(arenaHelpMenuHeaderMessage).send(player);
        verify(helpMenu).send(player, List.of(commandInfo));
        verify(arenaHelpMenuFooterMessage).send(player);
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
