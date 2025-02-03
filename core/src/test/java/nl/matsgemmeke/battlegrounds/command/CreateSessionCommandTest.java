package nl.matsgemmeke.battlegrounds.command;

import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.session.SessionConfiguration;
import nl.matsgemmeke.battlegrounds.game.session.SessionContext;
import nl.matsgemmeke.battlegrounds.game.session.SessionContextFactory;
import nl.matsgemmeke.battlegrounds.text.TextTemplate;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class CreateSessionCommandTest {

    private CommandSender sender;
    private GameContextProvider contextProvider;
    private SessionContextFactory sessionContextFactory;
    private Translator translator;

    @BeforeEach
    public void setUp() {
        this.sender = mock(CommandSender.class);
        this.contextProvider = mock(GameContextProvider.class);
        this.sessionContextFactory = mock(SessionContextFactory.class);
        this.translator = mock(Translator.class);

        when(translator.translate(TranslationKey.DESCRIPTION_CREATESESSION.getPath())).thenReturn(new TextTemplate("description"));
    }

    @Test
    public void shouldBeAbleToCreateSession() {
        int sessionId = 1;
        String message = "test";

        SessionContext context = mock(SessionContext.class);
        when(sessionContextFactory.make(eq(sessionId), any(SessionConfiguration.class))).thenReturn(context);

        when(contextProvider.addSessionContext(sessionId, context)).thenReturn(true);
        when(translator.translate(eq(TranslationKey.SESSION_CREATED.getPath()))).thenReturn(new TextTemplate(message));

        CreateSessionCommand command = new CreateSessionCommand(contextProvider, sessionContextFactory, translator);
        command.execute(sender, sessionId);

        verify(sender).sendMessage(message);
    }

    @Test
    public void shouldCancelSessionCreationIfProviderCanNotAddSession() {
        int sessionId = 1;
        String message = "test";

        SessionContext context = mock(SessionContext.class);
        when(sessionContextFactory.make(eq(sessionId), any(SessionConfiguration.class))).thenReturn(context);

        when(contextProvider.addSessionContext(sessionId, context)).thenReturn(false);
        when(translator.translate(eq(TranslationKey.SESSION_CREATION_FAILED.getPath()))).thenReturn(new TextTemplate(message));

        CreateSessionCommand command = new CreateSessionCommand(contextProvider, sessionContextFactory, translator);
        command.execute(sender, sessionId);

        verify(sender).sendMessage(message);
    }
}
