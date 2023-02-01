package com.github.matsgemmeke.battlegounds.command;

import com.github.matsgemmeke.battlegrounds.api.BattleContextProvider;
import com.github.matsgemmeke.battlegrounds.api.game.GameContext;
import com.github.matsgemmeke.battlegrounds.locale.PlaceholderEntry;
import com.github.matsgemmeke.battlegrounds.locale.TranslationKey;
import com.github.matsgemmeke.battlegrounds.locale.Translator;
import com.github.matsgemmeke.battlegrounds.command.CreateGameCommand;
import com.github.matsgemmeke.battlegrounds.game.GameContextFactory;
import org.bukkit.command.CommandSender;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.mockito.Mockito.*;

public class CreateGameCommandTest {

    private BattleContextProvider contextProvider;
    private CommandSender sender;
    private GameContextFactory contextFactory;
    private Translator translator;

    @Before
    public void setUp() throws IOException {
        this.contextProvider = mock(BattleContextProvider.class);
        this.sender = mock(CommandSender.class);
        this.contextFactory = mock(GameContextFactory.class);
        this.translator = mock(Translator.class);

        when(translator.translate(TranslationKey.DESCRIPTION_CREATEGAME.getPath())).thenReturn("description");
    }

    @Test
    public void shouldBeAbleToCreateGame() {
        int gameId = 1;
        String message = "test";

        when(contextProvider.addGameContext(any(GameContext.class))).thenReturn(true);
        when(translator.translate(eq(TranslationKey.GAME_CREATED.getPath()), any(PlaceholderEntry.class))).thenReturn(message);

        CreateGameCommand command = new CreateGameCommand(contextProvider, contextFactory, translator);
        command.execute(sender, gameId);

        verify(sender).sendMessage(message);
    }

    @Test
    public void shouldCancelGameCreationIfProviderCanNotAddGame() {
        int gameId = 1;
        String message = "test";

        when(contextProvider.addGameContext(any(GameContext.class))).thenReturn(false);
        when(translator.translate(eq(TranslationKey.GAME_CREATION_FAILED.getPath()), any(PlaceholderEntry.class))).thenReturn(message);

        CreateGameCommand command = new CreateGameCommand(contextProvider, contextFactory, translator);
        command.execute(sender, gameId);

        verify(sender).sendMessage(message);
    }
}
