package nl.matsgemmeke.battlegrounds.command;

import nl.matsgemmeke.battlegrounds.GameProvider;
import nl.matsgemmeke.battlegrounds.game.session.Session;
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
    private GameProvider gameProvider;
    private SessionFactory sessionFactory;
    private Translator translator;

    @Before
    public void setUp() throws IOException {
        this.sender = mock(CommandSender.class);
        this.gameProvider = mock(GameProvider.class);
        this.sessionFactory = mock(SessionFactory.class);
        this.translator = mock(Translator.class);

        when(translator.translate(TranslationKey.DESCRIPTION_CREATESESSION.getPath())).thenReturn("description");
    }

    @Test
    public void shouldBeAbleToCreateGame() {
        int gameId = 1;
        String message = "test";

        when(gameProvider.addSession(any())).thenReturn(true);
        when(translator.translate(eq(TranslationKey.SESSION_CREATED.getPath()), any(PlaceholderEntry.class))).thenReturn(message);

        CreateSessionCommand command = new CreateSessionCommand(gameProvider, sessionFactory, translator);
        command.execute(sender, gameId);

        verify(sender).sendMessage(message);
    }

    @Test
    public void shouldCancelGameCreationIfProviderCanNotAddGame() {
        int gameId = 1;
        String message = "test";

        when(gameProvider.addSession(any(Session.class))).thenReturn(false);
        when(translator.translate(eq(TranslationKey.SESSION_CREATION_FAILED.getPath()), any(PlaceholderEntry.class))).thenReturn(message);

        CreateSessionCommand command = new CreateSessionCommand(gameProvider, sessionFactory, translator);
        command.execute(sender, gameId);

        verify(sender).sendMessage(message);
    }
}
