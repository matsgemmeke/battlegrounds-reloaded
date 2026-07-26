package nl.matsgemmeke.battlegrounds.command.help;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HelpMenuTest {

    private static final String HEADER = "header";
    private static final String ENTRY_MESSAGE_TEXT = "/bg test <param>";
    private static final String ENTRY_HOVER_TEXT = "a description";
    private static final String ENTRY_SUGGESTION = "/bg test ";
    private static final String[] ENTRY_PERMISSIONS = { "battlegrounds.test" };
    private static final HelpMenuEntry ENTRY = new HelpMenuEntry(ENTRY_MESSAGE_TEXT, ENTRY_HOVER_TEXT, ENTRY_SUGGESTION, ENTRY_PERMISSIONS);
    private static final String FOOTER = "footer";

    @Mock
    private CommandSender sender;
    @Mock
    private Player player;

    @Test
    @DisplayName("send sends no message when help menu has no header, no footer and sender has no permission for any commands")
    void send_noPermissions() {
        when(sender.hasPermission(anyString())).thenReturn(false);

        HelpMenu.create()
                .entries(List.of(ENTRY))
                .send(sender);

        verify(sender, never()).sendMessage(anyString());
    }

    @Test
    @DisplayName("send sends JSON messages to player sender")
    void send_playerSender() {
        Player.Spigot spigot = mock(Player.Spigot.class);

        when(player.hasPermission(anyString())).thenReturn(true);
        when(player.spigot()).thenReturn(spigot);

        HelpMenu.create()
                .header(HEADER)
                .entries(List.of(ENTRY))
                .footer(FOOTER)
                .send(player);

        ArgumentCaptor<TextComponent> textComponentCaptor = ArgumentCaptor.forClass(TextComponent.class);
        verify(spigot).sendMessage(textComponentCaptor.capture());

        assertThat(textComponentCaptor.getValue()).satisfies(textComponent -> {
            assertThat(textComponent.getClickEvent()).satisfies(clickEvent -> {
                assertThat(clickEvent.getAction()).isEqualTo(ClickEvent.Action.SUGGEST_COMMAND);
                assertThat(clickEvent.getValue()).isEqualTo(ENTRY_SUGGESTION);
            });
            assertThat(textComponent.getHoverEvent()).satisfies(hoverEvent -> {
                assertThat(hoverEvent.getAction()).isEqualTo(HoverEvent.Action.SHOW_TEXT);
                assertThat(hoverEvent.getContents())
                        .extracting(content -> ((Text) content).getValue())
                        .contains(ENTRY_HOVER_TEXT);
            });
        });

        verify(player).sendMessage(HEADER);
        verify(player).sendMessage(FOOTER);
    }

    @Test
    @DisplayName("send sends normal messages to command sender")
    void sendHelpMenuAsNormalMessages() {
        when(sender.hasPermission(anyString())).thenReturn(true);

        HelpMenu.create()
                .header(HEADER)
                .entries(List.of(ENTRY))
                .footer(FOOTER)
                .send(sender);

        verify(sender).sendMessage(HEADER);
        verify(sender).sendMessage(ENTRY_MESSAGE_TEXT);
        verify(sender).sendMessage(FOOTER);
    }
}
