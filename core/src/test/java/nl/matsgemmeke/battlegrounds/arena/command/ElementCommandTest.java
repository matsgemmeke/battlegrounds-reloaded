package nl.matsgemmeke.battlegrounds.arena.command;

import nl.matsgemmeke.battlegrounds.arena.command.executor.element.AddSpawnPointCommandExecutor;
import nl.matsgemmeke.battlegrounds.arena.command.executor.element.RemoveElementCommandExecutor;
import nl.matsgemmeke.battlegrounds.command.CommandInfo;
import nl.matsgemmeke.battlegrounds.command.HelpMenu;
import nl.matsgemmeke.battlegrounds.i18n.Message;
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;
import org.bukkit.command.CommandSender;
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
class ElementCommandTest {

    private static final int ELEMENT_ID = 1;
    private static final int TEAM_ID = 2;

    @Mock
    private AddSpawnPointCommandExecutor addSpawnPointCommandExecutor;
    @Mock
    private RemoveElementCommandExecutor removeElementCommandExecutor;
    @Mock
    private HelpMenu helpMenu;
    @Mock
    private Player player;
    @Mock
    private Translator translator;
    @InjectMocks
    private ElementCommand command;

    @Test
    @DisplayName("onDefault sends unknown command message when command has args")
    void onDefault_withArgs() {
        String[] args = new String[] { "test" };
        Message message = mock(Message.class);

        when(translator.translateMessage(TranslationKey.UNKNOWN_COMMAND.getPath())).thenReturn(Optional.of(message));

        command.onDefault(player, args);

        verify(message).send(player);
    }

    @Test
    @DisplayName("onDefault shows help menu to player as JSON messages")
    void onDefault_playerSender() {
        CommandInfo commandInfo = new CommandInfo(null, null, null, new String[0]);
        Message headerMessage = mock(Message.class);
        Message footerMessage = mock(Message.class);

        when(translator.translateMessage(TranslationKey.ELEMENT_HELP_MENU_HEADER.getPath())).thenReturn(Optional.of(headerMessage));
        when(translator.translateMessage(TranslationKey.ELEMENT_HELP_MENU_FOOTER.getPath())).thenReturn(Optional.of(footerMessage));

        command.addCommandInfo(commandInfo);
        command.onDefault(player, null);

        verify(headerMessage).send(player);
        verify(helpMenu).sendHelpMenuAsJsonMessages(player, List.of(commandInfo));
        verify(footerMessage).send(player);
    }

    @Test
    @DisplayName("onDefault shows help menu to sender as normal messages")
    void onDefault_consoleSender() {
        CommandInfo commandInfo = new CommandInfo(null, null, null, new String[0]);
        CommandSender sender = mock(CommandSender.class);
        Message headerMessage = mock(Message.class);
        Message footerMessage = mock(Message.class);

        when(translator.translateMessage(TranslationKey.ELEMENT_HELP_MENU_HEADER.getPath())).thenReturn(Optional.of(headerMessage));
        when(translator.translateMessage(TranslationKey.ELEMENT_HELP_MENU_FOOTER.getPath())).thenReturn(Optional.of(footerMessage));

        command.addCommandInfo(commandInfo);
        command.onDefault(sender, null);

        verify(headerMessage).send(sender);
        verify(helpMenu).sendHelpMenuAsNormalMessages(sender, List.of(commandInfo));
        verify(footerMessage).send(sender);
    }

    @Test
    @DisplayName("onAdd sends specify element type message")
    void onAdd() {
        Message message = mock(Message.class);

        when(translator.translateMessage(TranslationKey.SPECIFY_ELEMENT_TYPE.getPath())).thenReturn(Optional.of(message));

        command.onAdd(player);

        verify(message).send(player);
    }

    @Test
    @DisplayName("onAddSpawnPoint delegates to AddSpawnPointCommandExecutor")
    void onAddSpawnPoint() {
        command.onAddSpawnPoint(player, TEAM_ID);

        verify(addSpawnPointCommandExecutor).execute(player, TEAM_ID);
    }

    @Test
    @DisplayName("onRemove delegates to RemoveElementCommandExecutor")
    void onRemove() {
        command.onRemove(player, ELEMENT_ID);

        verify(removeElementCommandExecutor).execute(player, ELEMENT_ID);
    }
}
