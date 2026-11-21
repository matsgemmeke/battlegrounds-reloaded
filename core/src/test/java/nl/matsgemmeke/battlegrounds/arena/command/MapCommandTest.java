package nl.matsgemmeke.battlegrounds.arena.command;

import nl.matsgemmeke.battlegrounds.arena.command.executor.CreateMapCommandExecutor;
import nl.matsgemmeke.battlegrounds.arena.command.executor.RemoveMapCommandExecutor;
import nl.matsgemmeke.battlegrounds.arena.command.executor.SelectMapCommandExecutor;
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

@ExtendWith(MockitoExtension.class)
class MapCommandTest {

    private static final int ARENA_ID = 1;
    private static final String MAP_NAME = "The Map";

    private static final String SUBCOMMAND_DESCRIPTION = "just a map command";
    private static final String SUBCOMMAND_USAGE = "/bg arena map test <nr>";
    private static final String SUBCOMMAND_SUGGESTION = "/bg arena map test ";
    private static final String[] SUBCOMMAND_PERMISSIONS = new String[0];

    @Mock
    private CreateMapCommandExecutor createMapCommandExecutor;
    @Mock
    private RemoveMapCommandExecutor removeMapCommandExecutor;
    @Mock
    private SelectMapCommandExecutor selectMapCommandExecutor;
    @Mock
    private HelpMenu helpMenu;
    @Mock
    private Player player;
    @Mock
    private Translator translator;
    @InjectMocks
    private MapCommand command;

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
        CommandInfo commandInfo = new CommandInfo(SUBCOMMAND_DESCRIPTION, SUBCOMMAND_USAGE, SUBCOMMAND_SUGGESTION, SUBCOMMAND_PERMISSIONS);
        Message mapHelpMenuHeaderMessage = mock(Message.class);
        Message mapHelpMenuFooterMessage = mock(Message.class);

        when(translator.translateMessage(TranslationKey.MAP_HELP_MENU_HEADER.getPath())).thenReturn(Optional.of(mapHelpMenuHeaderMessage));
        when(translator.translateMessage(TranslationKey.MAP_HELP_MENU_FOOTER.getPath())).thenReturn(Optional.of(mapHelpMenuFooterMessage));

        command.addCommandInfo(commandInfo);
        command.onDefault(player, null);

        verify(mapHelpMenuHeaderMessage).send(player);
        verify(helpMenu).send(player, List.of(commandInfo));
        verify(mapHelpMenuFooterMessage).send(player);
    }

    @Test
    @DisplayName("onCreate delegates to create map executor")
    void onCreate() {
        command.onCreate(player, ARENA_ID, MAP_NAME);

        verify(createMapCommandExecutor).execute(player, ARENA_ID, MAP_NAME);
    }

    @Test
    @DisplayName("onRemove delegates to remove map executor")
    void onRemove() {
        command.onRemove(player, ARENA_ID, MAP_NAME);

        verify(removeMapCommandExecutor).execute(player, ARENA_ID, MAP_NAME);
    }

    @Test
    @DisplayName("onSelect delegates to SelectMapCommandExecutor")
    void onSelect() {
        command.onSelect(player, ARENA_ID, MAP_NAME);

        verify(selectMapCommandExecutor).execute(player, ARENA_ID, MAP_NAME);
    }
}
