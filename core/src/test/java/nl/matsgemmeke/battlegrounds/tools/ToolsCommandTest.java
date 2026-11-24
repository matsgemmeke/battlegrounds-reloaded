package nl.matsgemmeke.battlegrounds.tools;

import nl.matsgemmeke.battlegrounds.command.CommandInfo;
import nl.matsgemmeke.battlegrounds.command.HelpMenu;
import nl.matsgemmeke.battlegrounds.i18n.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ToolsCommandTest {

    private static final String SUBCOMMAND_DESCRIPTION = "just a tools command";
    private static final String SUBCOMMAND_USAGE = "/bg tools test <nr>";
    private static final String SUBCOMMAND_SUGGESTION = "/bg tools test ";

    @Mock
    private CommandSender sender;
    @Mock
    private HelpMenu helpMenu;
    @Mock
    private ShowHitboxesCommandExecutor showHitboxesCommandExecutor;
    @Mock
    private Translator translator;
    @InjectMocks
    private ToolsCommand command;

    @Test
    @DisplayName("onDefault sends error message to command sender when args are not empty")
    void onDefault_withArgs() {
        Message toolNotExistsMessage = mock(Message.class);

        when(translator.translateMessage(eq(TranslationKey.TOOL_NOT_EXISTS.getPath()), any(Replacement[].class))).thenReturn(Optional.of(toolNotExistsMessage));

        command.onDefault(sender, new String[] { "test" });

        ArgumentCaptor<Replacement[]> replacementsCaptor = ArgumentCaptor.forClass(Replacement[].class);
        verify(translator).translateMessage(eq(TranslationKey.TOOL_NOT_EXISTS.getPath()), replacementsCaptor.capture());

        assertThat(replacementsCaptor.getValue()).satisfiesExactly(replacement -> {
            assertThat(replacement.key()).isEqualTo("tool");
            assertThat(replacement.value()).isEqualTo("test");
        });

        verify(toolNotExistsMessage).send(sender);
    }

    @Test
    @DisplayName("onDefault shows help menu to given console sender")
    void onDefault_noArgs() {
        CommandInfo commandInfo = new CommandInfo(SUBCOMMAND_DESCRIPTION, SUBCOMMAND_USAGE, SUBCOMMAND_SUGGESTION, new String[0]);
        Message toolsHelpMenuHeaderMessage = mock(Message.class);
        Message toolsHelpMenuFooterMessage = mock(Message.class);

        when(translator.translateMessage(TranslationKey.TOOLS_HELP_MENU_HEADER.getPath())).thenReturn(Optional.of(toolsHelpMenuHeaderMessage));
        when(translator.translateMessage(TranslationKey.TOOLS_HELP_MENU_FOOTER.getPath())).thenReturn(Optional.of(toolsHelpMenuFooterMessage));

        command.addCommandInfo(commandInfo);
        command.onDefault(sender, null);

        verify(toolsHelpMenuHeaderMessage).send(sender);
        verify(helpMenu).send(sender, List.of(commandInfo));
        verify(toolsHelpMenuFooterMessage).send(sender);
    }

    @Test
    @DisplayName("onShowHitboxes executes shot hitboxes command executor")
    void onShowHitboxes() {
        Player player = mock(Player.class);

        command.onShowHitboxes(player, 10, 20.0);

        verify(showHitboxesCommandExecutor).execute(player, 10, 20.0);
    }
}
