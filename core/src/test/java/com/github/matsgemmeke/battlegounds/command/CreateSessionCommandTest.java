package com.github.matsgemmeke.battlegounds.command;

import com.github.matsgemmeke.battlegrounds.api.BattleContextProvider;
import com.github.matsgemmeke.battlegrounds.api.game.Session;
import com.github.matsgemmeke.battlegrounds.command.CreateSessionCommand;
import com.github.matsgemmeke.battlegrounds.game.SessionFactory;
import com.github.matsgemmeke.battlegrounds.locale.PlaceholderEntry;
import com.github.matsgemmeke.battlegrounds.locale.TranslationKey;
import com.github.matsgemmeke.battlegrounds.locale.Translator;
import org.bukkit.command.CommandSender;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.mockito.Mockito.*;

public class CreateSessionCommandTest {

    private BattleContextProvider contextProvider;
    private CommandSender sender;
    private SessionFactory sessionFactory;
    private Translator translator;

    @Before
    public void setUp() throws IOException {
        this.contextProvider = mock(BattleContextProvider.class);
        this.sender = mock(CommandSender.class);
        this.sessionFactory = mock(SessionFactory.class);
        this.translator = mock(Translator.class);

        when(translator.translate(TranslationKey.DESCRIPTION_CREATESESSION.getPath())).thenReturn("description");
    }

    @Test
    public void shouldBeAbleToCreateGame() {
        int gameId = 1;
        String message = "test";

        when(contextProvider.addSession(any())).thenReturn(true);
        when(translator.translate(eq(TranslationKey.SESSION_CREATED.getPath()), any(PlaceholderEntry.class))).thenReturn(message);

        CreateSessionCommand command = new CreateSessionCommand(contextProvider, sessionFactory, translator);
        command.execute(sender, gameId);

        verify(sender).sendMessage(message);
    }

    @Test
    public void shouldCancelGameCreationIfProviderCanNotAddGame() {
        int gameId = 1;
        String message = "test";

        when(contextProvider.addSession(any(Session.class))).thenReturn(false);
        when(translator.translate(eq(TranslationKey.SESSION_CREATION_FAILED.getPath()), any(PlaceholderEntry.class))).thenReturn(message);

        CreateSessionCommand command = new CreateSessionCommand(contextProvider, sessionFactory, translator);
        command.execute(sender, gameId);

        verify(sender).sendMessage(message);
    }
}
