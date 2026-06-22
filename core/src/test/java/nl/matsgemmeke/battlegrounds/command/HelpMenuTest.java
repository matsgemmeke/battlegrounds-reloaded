package nl.matsgemmeke.battlegrounds.command;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import nl.matsgemmeke.battlegrounds.text.TextTemplate;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class HelpMenuTest {

    private static final String HELP_MENU_TITLE = "help menu";
    private static final String SUBCOMMAND_NAME = "test";
    private static final String SUBCOMMAND_DESCRIPTION = "just a test";
    private static final String SUBCOMMAND_USAGE = "/bg test <nr>";
    private static final String SUBCOMMAND_SUGGESTION = "/bg test";
    
    @Mock
    private Translator translator;
    @InjectMocks
    private HelpMenu helpMenu;
    @Captor
    private ArgumentCaptor<Map<String, Object>> valuesCaptor;

    @Test
    @DisplayName("sendHelpMenuAsJsonMessages shows help menu to given sender as normal messages")
    void sendHelpMenuAsNormalMessages() {
        CommandSender sender = mock(CommandSender.class);
        CommandInfo commandInfo = new CommandInfo(SUBCOMMAND_NAME, SUBCOMMAND_DESCRIPTION, SUBCOMMAND_USAGE, SUBCOMMAND_SUGGESTION, new String[0]);

        TextTemplate helpMenuCommandTextTemplate = mock(TextTemplate.class);
        when(helpMenuCommandTextTemplate.replace(anyMap())).thenReturn(" - /bg test <nr>");

        when(translator.translate(TranslationKey.HELP_MENU_COMMAND.getPath())).thenReturn(helpMenuCommandTextTemplate);

        helpMenu.sendHelpMenuAsNormalMessages(sender, HELP_MENU_TITLE, List.of(commandInfo));

        verify(helpMenuCommandTextTemplate).replace(valuesCaptor.capture());

        assertThat(valuesCaptor.getValue()).contains(
                entry("bg_name", SUBCOMMAND_NAME),
                entry("bg_description", SUBCOMMAND_DESCRIPTION),
                entry("bg_usage", SUBCOMMAND_USAGE)
        );

        verify(sender).sendMessage(HELP_MENU_TITLE);
        verify(sender).sendMessage(" - /bg test <nr>");
    }

    @Test
    @DisplayName("sendHelpMenuAsJsonMessages shows help menu to given player as JSON messages")
    void sendHelpMenuAsJsonMessages() {
        CommandInfo commandInfo = new CommandInfo(SUBCOMMAND_NAME, SUBCOMMAND_DESCRIPTION, SUBCOMMAND_USAGE, SUBCOMMAND_SUGGESTION, new String[0]);

        Player player = mock(Player.class);
        Player.Spigot spigot = mock(Player.Spigot.class);
        when(player.spigot()).thenReturn(spigot);

        TextTemplate helpMenuCommandTextTemplate = mock(TextTemplate.class);
        when(helpMenuCommandTextTemplate.replace(anyMap())).thenReturn(" - /bg test <nr>");

        when(translator.translate(TranslationKey.HELP_MENU_COMMAND.getPath())).thenReturn(helpMenuCommandTextTemplate);

        helpMenu.sendHelpMenuAsJsonMessages(player, HELP_MENU_TITLE, List.of(commandInfo));

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
                entry("bg_name", SUBCOMMAND_NAME),
                entry("bg_description", SUBCOMMAND_DESCRIPTION),
                entry("bg_usage", SUBCOMMAND_USAGE)
        );

        verify(player).sendMessage(HELP_MENU_TITLE);
    }
}
