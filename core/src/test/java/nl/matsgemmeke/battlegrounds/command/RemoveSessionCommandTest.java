package nl.matsgemmeke.battlegrounds.command;

import nl.matsgemmeke.battlegrounds.GameProvider;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.session.Session;
import nl.matsgemmeke.battlegrounds.locale.PlaceholderEntry;
import nl.matsgemmeke.battlegrounds.locale.TranslationKey;
import nl.matsgemmeke.battlegrounds.locale.Translator;
import org.bukkit.command.CommandSender;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class RemoveSessionCommandTest {

    private CommandSender sender;
    private GameProvider gameProvider;
    private Session session;
    private TaskRunner taskRunner;
    private Translator translator;

    @Before
    public void setUp() {
        this.sender = mock(CommandSender.class);
        this.gameProvider = mock(GameProvider.class);
        this.session = mock(Session.class);
        this.taskRunner = mock(TaskRunner.class);
        this.translator = mock(Translator.class);

        when(translator.translate(TranslationKey.DESCRIPTION_REMOVESESSION.getPath())).thenReturn("description");
    }

    @Test
    public void shouldAddSenderToConfirmListUponFirstExecutingCommand() {
        int gameId = 1;

        RemoveSessionCommand command = new RemoveSessionCommand(gameProvider, taskRunner, translator);
        command.execute(sender, gameId);

        verify(taskRunner).runTaskLater(any(Runnable.class), anyLong());
    }

    @Test
    public void shouldBeAbleToRemoveSession() {
        int sessionId = 1;
        String message = "hello";

        when(gameProvider.getSession(sessionId)).thenReturn(session);
        when(gameProvider.removeSession(session)).thenReturn(true);
        when(translator.translate(eq(TranslationKey.SESSION_REMOVED.getPath()), any(PlaceholderEntry.class))).thenReturn(message);

        RemoveSessionCommand command = new RemoveSessionCommand(gameProvider, taskRunner, translator);
        command.execute(sender, sessionId);
        command.execute(sender, sessionId);

        verify(sender).sendMessage(message);
    }

    @Test
    public void shouldNotifySenderWhenFailingToCreateSession() {
        int sessionId = 1;
        String message = "hello";

        when(gameProvider.getSession(sessionId)).thenReturn(session);
        when(gameProvider.removeSession(session)).thenReturn(false);
        when(translator.translate(eq(TranslationKey.SESSION_REMOVAL_FAILED.getPath()), any(PlaceholderEntry.class))).thenReturn(message);

        RemoveSessionCommand command = new RemoveSessionCommand(gameProvider, taskRunner, translator);
        command.execute(sender, sessionId);
        command.execute(sender, sessionId);

        verify(sender).sendMessage(message);
    }
}
