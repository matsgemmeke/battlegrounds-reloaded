package nl.matsgemmeke.battlegrounds.command;

import net.kyori.adventure.audience.Audience;
import nl.matsgemmeke.battlegrounds.i18n.*;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class HelpMenuTest {

    private static final String SUBCOMMAND_DESCRIPTION = "just a test";
    private static final String SUBCOMMAND_USAGE = "/bg test <nr>";
    private static final String SUBCOMMAND_SUGGESTION = "/bg test";
    private static final String SUBCOMMAND_PERMISSION = "battlegrounds.test";
    private static final String[] SUBCOMMAND_PERMISSIONS = { SUBCOMMAND_PERMISSION };

    @Mock
    private Translator translator;
    @InjectMocks
    private HelpMenu helpMenu;

    @Test
    @DisplayName("send does nothing for given empty command list")
    void send_empty() {
        Audience audience = mock(Audience.class);

        helpMenu.send(audience, List.of());

        verifyNoInteractions(translator);
    }

    @Test
    @DisplayName("send does not send any messages when given audience does not have permissions for any command")
    void send_notPermitted() {
        CommandInfo commandInfo = new CommandInfo(SUBCOMMAND_DESCRIPTION, SUBCOMMAND_USAGE, SUBCOMMAND_SUGGESTION, SUBCOMMAND_PERMISSIONS);

        Player player = mock(Player.class);
        when(player.hasPermission(SUBCOMMAND_PERMISSION)).thenReturn(false);
        doCallRealMethod().when(player).forEachAudience(any());

        helpMenu.send(player, List.of(commandInfo));

        verifyNoInteractions(translator);
    }

    @Test
    @DisplayName("send sends messages to all audience members for commands without permissions")
    void send_noPermission() {
        CommandInfo commandInfo = new CommandInfo(SUBCOMMAND_DESCRIPTION, SUBCOMMAND_USAGE, SUBCOMMAND_SUGGESTION, new String[0]);
        Message helpMenuCommandMessage = mock(Message.class);

        Player permittedPlayer = mock(Player.class);
        doCallRealMethod().when(permittedPlayer).forEachAudience(any());

        when(translator.translateMessage(eq(TranslationKey.HELP_MENU_COMMAND.getPath()), any(Replacement[].class))).thenReturn(Optional.of(helpMenuCommandMessage));

        helpMenu.send(permittedPlayer, List.of(commandInfo));

        verify(helpMenuCommandMessage).send(permittedPlayer);
    }

    @Test
    @DisplayName("send sends messages to all audience members who have permission")
    void send_permitted() {
        CommandInfo commandInfoWithDescription = new CommandInfo(SUBCOMMAND_DESCRIPTION, SUBCOMMAND_USAGE, SUBCOMMAND_SUGGESTION, SUBCOMMAND_PERMISSIONS);
        CommandInfo commandInfoWithoutDescription = new CommandInfo(null, "/bg descriptionless", "bg descriptionless", new String[] { "battlegrounds.descriptionless" });
        Message helpMenuCommandMessage = mock(Message.class);

        Player permittedPlayer = mock(Player.class);
        when(permittedPlayer.hasPermission(anyString())).thenReturn(true);
        doCallRealMethod().when(permittedPlayer).forEachAudience(any());

        Player nonPermittedPlayer = mock(Player.class);
        when(nonPermittedPlayer.hasPermission(anyString())).thenReturn(false);
        doCallRealMethod().when(nonPermittedPlayer).forEachAudience(any());

        Audience audience = Audience.audience(permittedPlayer, nonPermittedPlayer);

        when(translator.translateMessage(eq(TranslationKey.HELP_MENU_COMMAND.getPath()), any(Replacement[].class))).thenReturn(Optional.of(helpMenuCommandMessage));

        helpMenu.send(audience, List.of(commandInfoWithDescription, commandInfoWithoutDescription));

        ArgumentCaptor<Replacement[]> replacementsCaptor = ArgumentCaptor.forClass(Replacement[].class);
        verify(translator, times(2)).translateMessage(eq(TranslationKey.HELP_MENU_COMMAND.getPath()), replacementsCaptor.capture());

        assertThat(replacementsCaptor.getAllValues()).satisfiesExactly(replacements -> {
            assertThat(replacements).satisfiesExactly(replacement -> {
                assertThat(replacement.key()).isEqualTo("bg-description");
                assertThat(replacement.value()).isEqualTo(SUBCOMMAND_DESCRIPTION);
            }, replacement -> {
                assertThat(replacement.key()).isEqualTo("bg-usage");
                assertThat(replacement.value()).isEqualTo(SUBCOMMAND_USAGE);
            }, replacement -> {
                assertThat(replacement.key()).isEqualTo("bg-suggestion");
                assertThat(replacement.value()).isEqualTo(SUBCOMMAND_SUGGESTION);
            });
        }, replacements -> {
            assertThat(replacements).satisfiesExactly(replacement -> {
                assertThat(replacement.key()).isEqualTo("bg-description");
                assertThat(replacement.value()).isBlank();
            }, replacement -> {
                assertThat(replacement.key()).isEqualTo("bg-usage");
                assertThat(replacement.value()).isEqualTo("/bg descriptionless");
            }, replacement -> {
                assertThat(replacement.key()).isEqualTo("bg-suggestion");
                assertThat(replacement.value()).isEqualTo("bg descriptionless");
            });
        });

        verify(helpMenuCommandMessage, times(2)).send(permittedPlayer);
        verify(helpMenuCommandMessage, never()).send(nonPermittedPlayer);
    }
}
