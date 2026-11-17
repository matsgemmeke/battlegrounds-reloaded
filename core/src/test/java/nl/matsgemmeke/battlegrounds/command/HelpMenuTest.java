package nl.matsgemmeke.battlegrounds.command;

import net.kyori.adventure.audience.Audience;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import nl.matsgemmeke.battlegrounds.i18n.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyMap;
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
    @Captor
    private ArgumentCaptor<Map<String, Object>> valuesCaptor;

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
        CommandInfo commandInfo = new CommandInfo(SUBCOMMAND_DESCRIPTION, SUBCOMMAND_USAGE, SUBCOMMAND_SUGGESTION, SUBCOMMAND_PERMISSIONS);
        Message helpMenuCommandMessage = mock(Message.class);

        Player permittedPlayer = mock(Player.class);
        when(permittedPlayer.hasPermission(SUBCOMMAND_PERMISSION)).thenReturn(true);
        doCallRealMethod().when(permittedPlayer).forEachAudience(any());

        Player nonPermittedPlayer = mock(Player.class);
        when(nonPermittedPlayer.hasPermission(SUBCOMMAND_PERMISSION)).thenReturn(false);
        doCallRealMethod().when(nonPermittedPlayer).forEachAudience(any());

        Audience audience = Audience.audience(permittedPlayer, nonPermittedPlayer);

        when(translator.translateMessage(eq(TranslationKey.HELP_MENU_COMMAND.getPath()), any(Replacement[].class))).thenReturn(Optional.of(helpMenuCommandMessage));

        helpMenu.send(audience, List.of(commandInfo));

        verify(helpMenuCommandMessage).send(permittedPlayer);
        verify(helpMenuCommandMessage, never()).send(nonPermittedPlayer);
    }

    @Test
    @DisplayName("sendHelpMenuAsJsonMessages shows help menu with permitted commands to given sender as normal messages")
    void sendHelpMenuAsNormalMessages() {
        CommandInfo permittedCommandInfo = new CommandInfo(SUBCOMMAND_DESCRIPTION, SUBCOMMAND_USAGE, SUBCOMMAND_SUGGESTION, SUBCOMMAND_PERMISSIONS);
        CommandInfo forbiddenCommandInfo = new CommandInfo(null, null, null, new String[] { "battlegrounds.forbidden" });

        CommandSender sender = mock(CommandSender.class);
        when(sender.hasPermission("battlegrounds.test")).thenReturn(true);
        when(sender.hasPermission("battlegrounds.forbidden")).thenReturn(false);

        TextTemplate helpMenuCommandTextTemplate = mock(TextTemplate.class);
        when(helpMenuCommandTextTemplate.replace(anyMap())).thenReturn(" - /bg test <nr>");

        when(translator.translate(TranslationKey.HELP_MENU_COMMAND.getPath())).thenReturn(helpMenuCommandTextTemplate);

        helpMenu.sendHelpMenuAsNormalMessages(sender, List.of(permittedCommandInfo, forbiddenCommandInfo));

        verify(helpMenuCommandTextTemplate).replace(valuesCaptor.capture());

        assertThat(valuesCaptor.getValue()).contains(
                entry("bg_description", SUBCOMMAND_DESCRIPTION),
                entry("bg_usage", SUBCOMMAND_USAGE)
        );

        verify(sender).sendMessage(" - /bg test <nr>");
    }

    @Test
    @DisplayName("sendHelpMenuAsJsonMessages shows help menu with permitted commands to given player as JSON messages")
    void sendHelpMenuAsJsonMessages() {
        CommandInfo permittedCommandInfo = new CommandInfo(SUBCOMMAND_DESCRIPTION, SUBCOMMAND_USAGE, SUBCOMMAND_SUGGESTION, SUBCOMMAND_PERMISSIONS);
        CommandInfo forbiddenCommandInfo = new CommandInfo(null, null, null, new String[] { "battlegrounds.forbidden" });
        Player.Spigot spigot = mock(Player.Spigot.class);

        Player player = mock(Player.class);
        when(player.hasPermission("battlegrounds.test")).thenReturn(true);
        when(player.hasPermission("battlegrounds.forbidden")).thenReturn(false);
        when(player.spigot()).thenReturn(spigot);

        TextTemplate helpMenuCommandTextTemplate = mock(TextTemplate.class);
        when(helpMenuCommandTextTemplate.replace(anyMap())).thenReturn(" - /bg test <nr>");

        when(translator.translate(TranslationKey.HELP_MENU_COMMAND.getPath())).thenReturn(helpMenuCommandTextTemplate);

        helpMenu.sendHelpMenuAsJsonMessages(player, List.of(permittedCommandInfo, forbiddenCommandInfo));

        ArgumentCaptor<TextComponent> textComponentCaptor = ArgumentCaptor.forClass(TextComponent.class);
        verify(spigot).sendMessage(textComponentCaptor.capture());

        TextComponent textComponent = textComponentCaptor.getValue();
        assertThat(textComponent.getClickEvent()).satisfies(clickEvent -> {
            assertThat(clickEvent.getAction()).isEqualTo(ClickEvent.Action.SUGGEST_COMMAND);
            assertThat(clickEvent.getValue()).isEqualTo(SUBCOMMAND_SUGGESTION);
        });
        assertThat(textComponent.getHoverEvent()).satisfies(hoverEvent -> {
            assertThat(hoverEvent.getAction()).isEqualTo(HoverEvent.Action.SHOW_TEXT);
            assertThat(hoverEvent.getContents())
                    .extracting(content -> ((Text) content).getValue())
                    .contains(SUBCOMMAND_DESCRIPTION);
        });

        verify(helpMenuCommandTextTemplate).replace(valuesCaptor.capture());

        assertThat(valuesCaptor.getValue()).contains(
                entry("bg_description", SUBCOMMAND_DESCRIPTION),
                entry("bg_usage", SUBCOMMAND_USAGE)
        );
    }
}
