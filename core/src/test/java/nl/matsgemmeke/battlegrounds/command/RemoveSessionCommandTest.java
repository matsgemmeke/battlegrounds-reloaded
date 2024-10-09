package nl.matsgemmeke.battlegrounds.command;

import nl.matsgemmeke.battlegrounds.GameContextProvider;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.text.PlaceholderEntry;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.bukkit.command.CommandSender;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class RemoveSessionCommandTest {

    private CommandSender sender;
    private GameContext sessionContext;
    private GameContextProvider contextProvider;
    private TaskRunner taskRunner;
    private Translator translator;

    @Before
    public void setUp() {
        this.sender = mock(CommandSender.class);
        this.sessionContext = mock(GameContext.class);
        this.contextProvider = mock(GameContextProvider.class);
        this.taskRunner = mock(TaskRunner.class);
        this.translator = mock(Translator.class);

        when(translator.translate(TranslationKey.DESCRIPTION_REMOVESESSION.getPath())).thenReturn("description");
    }

    @Test
    public void shouldAddSenderToConfirmListUponFirstExecutingCommand() {
        int gameId = 1;

        RemoveSessionCommand command = new RemoveSessionCommand(contextProvider, taskRunner, translator);
        command.execute(sender, gameId);

        verify(taskRunner).runTaskLater(any(Runnable.class), anyLong());
    }

    @Test
    public void shouldBeAbleToRemoveSession() {
        int sessionId = 1;
        String message = "hello";

        when(contextProvider.getSessionContext(sessionId)).thenReturn(sessionContext);
        when(contextProvider.removeSessionContext(sessionId)).thenReturn(true);
        when(translator.translate(eq(TranslationKey.SESSION_REMOVED.getPath()), any(PlaceholderEntry.class))).thenReturn(message);

        RemoveSessionCommand command = new RemoveSessionCommand(contextProvider, taskRunner, translator);
        command.execute(sender, sessionId);
        command.execute(sender, sessionId);

        verify(sender).sendMessage(message);
    }

    @Test
    public void shouldNotifySenderWhenFailingToCreateSession() {
        int sessionId = 1;
        String message = "hello";

        when(contextProvider.getSessionContext(sessionId)).thenReturn(sessionContext);
        when(contextProvider.removeSessionContext(sessionId)).thenReturn(false);
        when(translator.translate(eq(TranslationKey.SESSION_REMOVAL_FAILED.getPath()), any(PlaceholderEntry.class))).thenReturn(message);

        RemoveSessionCommand command = new RemoveSessionCommand(contextProvider, taskRunner, translator);
        command.execute(sender, sessionId);
        command.execute(sender, sessionId);

        verify(sender).sendMessage(message);
    }
}
