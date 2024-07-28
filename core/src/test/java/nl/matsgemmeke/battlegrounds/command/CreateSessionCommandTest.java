package nl.matsgemmeke.battlegrounds.command;

import nl.matsgemmeke.battlegrounds.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.session.Session;
import nl.matsgemmeke.battlegrounds.game.session.SessionConfiguration;
import nl.matsgemmeke.battlegrounds.game.session.SessionFactory;
import nl.matsgemmeke.battlegrounds.locale.PlaceholderEntry;
import nl.matsgemmeke.battlegrounds.locale.TranslationKey;
import nl.matsgemmeke.battlegrounds.locale.Translator;
import org.bukkit.command.CommandSender;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.mockito.Mockito.*;

public class CreateSessionCommandTest {

    private CommandSender sender;
    private GameContextProvider contextProvider;
    private SessionFactory sessionFactory;
    private Translator translator;

    @Before
    public void setUp() throws IOException {
        this.sender = mock(CommandSender.class);
        this.contextProvider = mock(GameContextProvider.class);
        this.sessionFactory = mock(SessionFactory.class);
        this.translator = mock(Translator.class);

        when(translator.translate(TranslationKey.DESCRIPTION_CREATESESSION.getPath())).thenReturn("description");
    }

    @Test
    public void shouldBeAbleToCreateSession() {
        int sessionId = 1;
        String message = "test";

        GameContext context = mock(GameContext.class);

        Session session = mock(Session.class);
        when(session.getContext()).thenReturn(context);
        when(sessionFactory.make(eq(sessionId), any(SessionConfiguration.class))).thenReturn(session);

        when(contextProvider.addSessionContext(sessionId, context)).thenReturn(true);
        when(translator.translate(eq(TranslationKey.SESSION_CREATED.getPath()), any(PlaceholderEntry.class))).thenReturn(message);

        CreateSessionCommand command = new CreateSessionCommand(contextProvider, sessionFactory, translator);
        command.execute(sender, sessionId);

        verify(sender).sendMessage(message);
    }

    @Test
    public void shouldCancelSessionCreationIfProviderCanNotAddSession() {
        int sessionId = 1;
        String message = "test";

        GameContext context = mock(GameContext.class);

        Session session = mock(Session.class);
        when(session.getContext()).thenReturn(context);
        when(sessionFactory.make(eq(sessionId), any(SessionConfiguration.class))).thenReturn(session);

        when(contextProvider.addSessionContext(sessionId, context)).thenReturn(false);
        when(translator.translate(eq(TranslationKey.SESSION_CREATION_FAILED.getPath()), any(PlaceholderEntry.class))).thenReturn(message);

        CreateSessionCommand command = new CreateSessionCommand(contextProvider, sessionFactory, translator);
        command.execute(sender, sessionId);

        verify(sender).sendMessage(message);
    }
}
