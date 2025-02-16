package nl.matsgemmeke.battlegrounds.command;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.text.TextTemplate;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class RemoveSessionCommandTest {

    private CommandSender sender;
    private GameContextProvider contextProvider;
    private TaskRunner taskRunner;
    private Translator translator;

    @BeforeEach
    public void setUp() {
        this.sender = mock(CommandSender.class);
        this.contextProvider = mock(GameContextProvider.class);
        this.taskRunner = mock(TaskRunner.class);
        this.translator = mock(Translator.class);

        when(translator.translate(TranslationKey.DESCRIPTION_REMOVESESSION.getPath())).thenReturn(new TextTemplate("description"));
    }

    @Test
    public void executeAddsSenderToConfirmListUponFirstExecutingCommand() {
        int gameId = 1;
        String confirmMessage = "confirm removal";

        when(translator.translate(TranslationKey.SESSION_CONFIRM_REMOVAL.getPath())).thenReturn(new TextTemplate(confirmMessage));

        RemoveSessionCommand command = new RemoveSessionCommand(contextProvider, taskRunner, translator);
        command.execute(sender, gameId);

        verify(sender).sendMessage(confirmMessage);
        verify(taskRunner).runTaskLater(any(Runnable.class), anyLong());
    }

    @Test
    public void executeRemovesSession() {
        int sessionId = 1;
        String message = "hello";

        when(contextProvider.removeSession(sessionId)).thenReturn(true);
        when(translator.translate(TranslationKey.SESSION_CONFIRM_REMOVAL.getPath())).thenReturn(new TextTemplate("test"));
        when(translator.translate(TranslationKey.SESSION_REMOVED.getPath())).thenReturn(new TextTemplate(message));

        RemoveSessionCommand command = new RemoveSessionCommand(contextProvider, taskRunner, translator);
        command.execute(sender, sessionId);
        command.execute(sender, sessionId);

        verify(sender).sendMessage(message);
    }

    @Test
    public void executeNotifiesSenderWhenFailingToCreateSession() {
        int sessionId = 1;
        String message = "hello";

        when(contextProvider.removeSession(sessionId)).thenReturn(false);
        when(translator.translate(TranslationKey.SESSION_CONFIRM_REMOVAL.getPath())).thenReturn(new TextTemplate("test"));
        when(translator.translate(TranslationKey.SESSION_REMOVAL_FAILED.getPath())).thenReturn(new TextTemplate(message));

        RemoveSessionCommand command = new RemoveSessionCommand(contextProvider, taskRunner, translator);
        command.execute(sender, sessionId);
        command.execute(sender, sessionId);

        verify(sender).sendMessage(message);
    }
}
