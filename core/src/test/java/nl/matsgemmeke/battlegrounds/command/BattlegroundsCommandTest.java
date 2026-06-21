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

import java.util.Map;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BattlegroundsCommandTest {

    private static final int ARENA_ID = 1;
    private static final String HELP_MENU_TITLE = "help menu";

    private static final String SUBCOMMAND_NAME = "test";
    private static final String SUBCOMMAND_DESCRIPTION = "just a test";
    private static final String SUBCOMMAND_USAGE = "bg test <nr>";

    @Mock
    private Player player;
    @Mock
    private Translator translator;
    @InjectMocks
    private BattlegroundsCommand bgCommand;
    @Captor
    private ArgumentCaptor<Map<String, Object>> valuesCaptor;

    @Test
    void onReloadThrowsIllegalArgumentExceptionWhenSubcommandDoesNotExist() {
        CommandSource subcommand = mock(CommandSource.class);
        when(subcommand.getName()).thenReturn("not the reload command");

        bgCommand.addSubcommand(subcommand);

        assertThatThrownBy(() -> bgCommand.onReload(player))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unable to find a subcommand by the name reload");
    }

    @Test
    void onCatchUnknownSendsErrorMessageToCommandSender() {
        when(translator.translate(TranslationKey.UNKNOWN_COMMAND.getPath())).thenReturn(new TextTemplate("test"));

        bgCommand.onCatchUnknown(player);

        verify(player).sendMessage("test");
    }

    @Test
    @DisplayName("onDefault shows help menu to player as JSON messages")
    void onDefault_playerSender() {
        ReloadCommand command = mock(ReloadCommand.class);
        when(command.getName()).thenReturn(SUBCOMMAND_NAME);
        when(command.getDescription()).thenReturn(SUBCOMMAND_DESCRIPTION);
        when(command.getUsage()).thenReturn(SUBCOMMAND_USAGE);

        Player player = mock(Player.class);
        Player.Spigot spigot = mock(Player.Spigot.class);
        when(player.spigot()).thenReturn(spigot);

        TextTemplate helpMenuCommandTextTemplate = mock(TextTemplate.class);
        when(helpMenuCommandTextTemplate.replace(anyMap())).thenReturn(" - /bg test <nr>");

        when(translator.translate(TranslationKey.HELP_MENU_TITLE.getPath())).thenReturn(new TextTemplate(HELP_MENU_TITLE));
        when(translator.translate(TranslationKey.HELP_MENU_COMMAND.getPath())).thenReturn(helpMenuCommandTextTemplate);

        bgCommand.addSubcommand(command);
        bgCommand.onDefault(player);

        ArgumentCaptor<TextComponent> textComponentCaptor = ArgumentCaptor.forClass(TextComponent.class);
        verify(spigot).sendMessage(textComponentCaptor.capture());

        TextComponent textComponent = textComponentCaptor.getValue();
        assertThat(textComponent.getClickEvent()).satisfies(clickEvent -> {
            assertThat(clickEvent.getAction()).isEqualTo(ClickEvent.Action.SUGGEST_COMMAND);
            assertThat(clickEvent.getValue()).isEqualTo("/bg test <nr>");
        });
        assertThat(textComponent.getHoverEvent()).satisfies(hoverEvent -> {
            assertThat(hoverEvent.getAction()).isEqualTo(HoverEvent.Action.SHOW_TEXT);
            assertThat(hoverEvent.getContents())
                    .extracting(content -> ((Text) content).getValue())
                    .contains(SUBCOMMAND_DESCRIPTION);
        });

        verify(player).sendMessage(HELP_MENU_TITLE);
    }

    @Test
    @DisplayName("onDefault shows help menu to sender as normal messages")
    void onDefault_consoleSender() {
        CommandSender sender = mock(CommandSender.class);

        TextTemplate helpMenuCommandTextTemplate = mock(TextTemplate.class);
        when(helpMenuCommandTextTemplate.replace(anyMap())).thenReturn(" - /bg test <nr>");

        ReloadCommand command = mock(ReloadCommand.class);
        when(command.getName()).thenReturn(SUBCOMMAND_NAME);
        when(command.getDescription()).thenReturn(SUBCOMMAND_DESCRIPTION);
        when(command.getUsage()).thenReturn(SUBCOMMAND_USAGE);

        when(translator.translate(TranslationKey.HELP_MENU_TITLE.getPath())).thenReturn(new TextTemplate(HELP_MENU_TITLE));
        when(translator.translate(TranslationKey.HELP_MENU_COMMAND.getPath())).thenReturn(helpMenuCommandTextTemplate);

        bgCommand.addSubcommand(command);
        bgCommand.onDefault(sender);

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
    @DisplayName("onCreateArena executes create arena command")
    void onCreateArena() {
        CreateArenaCommand command = mock(CreateArenaCommand.class);
        when(command.getName()).thenReturn("createarena");

        bgCommand.addSubcommand(command);
        bgCommand.onCreateArena(player, ARENA_ID);

        verify(command).execute(player, ARENA_ID);
    }

    @Test
    void onGiveWeaponExecutesGiveWeaponCommand() {
        String[] args = { "test", "weapon" };

        GiveWeaponCommand command = mock(GiveWeaponCommand.class);
        when(command.getName()).thenReturn("giveweapon");

        bgCommand.addSubcommand(command);
        bgCommand.onGiveWeapon(player, args);

        verify(command).execute(player, args);
    }

    @Test
    void onReloadExecutesReloadCommand() {
        ReloadCommand command = mock(ReloadCommand.class);
        when(command.getName()).thenReturn("reload");

        bgCommand.addSubcommand(command);
        bgCommand.onReload(player);

        verify(command).execute(player);
    }

    @Test
    @DisplayName("onRemoveArena executes remove arena command")
    void onRemoveArena() {
        RemoveArenaCommand command = mock(RemoveArenaCommand.class);
        when(command.getName()).thenReturn("removearena");

        bgCommand.addSubcommand(command);
        bgCommand.onRemoveArena(player, ARENA_ID);

        verify(command).execute(player, ARENA_ID);
    }

    @Test
    void onSetMainLobbyExecutesSetMainLobbyCommand() {
        SetMainLobbyCommand command = mock(SetMainLobbyCommand.class);
        when(command.getName()).thenReturn("setmainlobby");

        bgCommand.addSubcommand(command);
        bgCommand.onSetMainLobby(player);

        verify(command).execute(player);
    }
}
