package nl.matsgemmeke.battlegrounds.command;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import nl.matsgemmeke.battlegrounds.command.tool.ShowHitboxesTool;
import nl.matsgemmeke.battlegrounds.text.TextTemplate;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Player.Spigot;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ToolsCommandTest {

    @Mock
    private ShowHitboxesTool showHitboxesTool;
    @Mock
    private Translator translator;
    @InjectMocks
    private ToolsCommand command;

    @Test
    void onDefaultSendsErrorMessageToCommandSenderWhenArgsAreDefined() {
        CommandSender commandSender = mock(CommandSender.class);

        when(translator.translate(TranslationKey.TOOL_NOT_EXISTS.getPath())).thenReturn(new TextTemplate("&cA tool by the name of %bg_tool% doesn't exist."));

        command.onDefault(commandSender, new String[] { "test" });

        verify(commandSender).sendMessage("&cA tool by the name of test doesn't exist.");
    }

    @Test
    void onDefaultSendToolsMenuToPlayerWhenNoArgsAreDefined() {
        Spigot spigot = mock(Spigot.class);

        Player player = mock(Player.class);
        when(player.spigot()).thenReturn(spigot);

        when(translator.translate(TranslationKey.TOOLS_MENU_TITLE.getPath())).thenReturn(new TextTemplate("&6&l List of tools"));
        when(translator.translate(TranslationKey.TOOLS_MENU_COMMAND.getPath())).thenReturn(new TextTemplate(" ● /bg tools hitbox <seconds> <range>"));
        when(translator.translate(TranslationKey.DESCRIPTION_TOOLS_HITBOX.getPath())).thenReturn(new TextTemplate("Shows hitboxes of nearby entities"));

        command.onDefault(player, new String[0]);

        ArgumentCaptor<TextComponent> textComponentCaptor = ArgumentCaptor.forClass(TextComponent.class);
        verify(spigot).sendMessage(textComponentCaptor.capture());

        assertThat(textComponentCaptor.getValue()).satisfies(textComponent -> {
            assertThat(textComponent.getText()).isEqualTo(" ● /bg tools hitbox <seconds> <range>");
            assertThat(textComponent.getClickEvent().getAction()).isEqualTo(ClickEvent.Action.SUGGEST_COMMAND);
            assertThat(textComponent.getClickEvent().getValue()).isEqualTo("/bg tools hitbox");
            assertThat(textComponent.getHoverEvent().getAction()).isEqualTo(HoverEvent.Action.SHOW_TEXT);
            assertThat(textComponent.getHoverEvent().getContents()).satisfiesExactly(content -> {
                assertThat(content).isInstanceOf(Text.class);
                assertThat(((Text) content).getValue()).isEqualTo("Shows hitboxes of nearby entities");
            });
        });
    }

    @Test
    void onDefaultSendToolsMenuToCommandSenderWhenNoArgsAreDefined() {
        CommandSender commandSender = mock(CommandSender.class);

        when(translator.translate(TranslationKey.TOOLS_MENU_TITLE.getPath())).thenReturn(new TextTemplate("&6&l List of tools"));
        when(translator.translate(TranslationKey.TOOLS_MENU_COMMAND.getPath())).thenReturn(new TextTemplate(" ● /bg tools hitbox <seconds> <range>"));
        when(translator.translate(TranslationKey.DESCRIPTION_TOOLS_HITBOX.getPath())).thenReturn(new TextTemplate("Shows hitboxes of nearby entities"));

        command.onDefault(commandSender, new String[0]);

        verify(commandSender).sendMessage("&6&l List of tools");
        verify(commandSender).sendMessage(" ● /bg tools hitbox <seconds> <range>");
    }

    @Test
    void onHitboxExecutesShowHitboxesTool() {
        Player player = mock(Player.class);

        command.onHitbox(player, 10, 20.0);

        verify(showHitboxesTool).execute(player, 10, 20.0);
    }
}
