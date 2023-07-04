package com.github.matsgemmeke.battlegounds.command;

import com.github.matsgemmeke.battlegrounds.*;
import com.github.matsgemmeke.battlegrounds.api.BattleContextProvider;
import com.github.matsgemmeke.battlegrounds.api.game.GameContext;
import com.github.matsgemmeke.battlegrounds.command.RemoveGameCommand;
import com.github.matsgemmeke.battlegrounds.locale.PlaceholderEntry;
import com.github.matsgemmeke.battlegrounds.locale.TranslationKey;
import com.github.matsgemmeke.battlegrounds.locale.Translator;
import org.bukkit.command.CommandSender;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class RemoveGameCommandTest {

    private BattleContextProvider contextProvider;
    private CommandSender sender;
    private GameContext context;
    private TaskRunner taskRunner;
    private Translator translator;

    @Before
    public void setUp() {
        this.contextProvider = mock(BattleContextProvider.class);
        this.sender = mock(CommandSender.class);
        this.context = mock(GameContext.class);
        this.taskRunner = mock(TaskRunner.class);
        this.translator = mock(Translator.class);

        when(translator.translate(TranslationKey.DESCRIPTION_REMOVEGAME.getPath())).thenReturn("description");
    }

    @Test
    public void shouldAddSenderToConfirmListUponFirstExecutingCommand() {
        int gameId = 1;

        RemoveGameCommand command = new RemoveGameCommand(contextProvider, taskRunner, translator);
        command.execute(sender, gameId);

        verify(taskRunner).runTaskLater(any(Runnable.class), anyLong());
    }

    @Test
    public void shouldBeAbleToRemoveGame() {
        int gameId = 1;
        String message = "hello";

        when(contextProvider.getGameContext(gameId)).thenReturn(context);
        when(contextProvider.removeGameContext(context)).thenReturn(true);
        when(translator.translate(eq(TranslationKey.GAME_REMOVED.getPath()), any(PlaceholderEntry.class))).thenReturn(message);

        RemoveGameCommand command = new RemoveGameCommand(contextProvider, taskRunner, translator);
        command.execute(sender, gameId);
        command.execute(sender, gameId);

        verify(sender).sendMessage(message);
    }

    @Test
    public void shouldNotifySenderWhenGameFailedToCreate() {
        int gameId = 1;
        String message = "hello";

        when(contextProvider.getGameContext(gameId)).thenReturn(context);
        when(contextProvider.removeGameContext(context)).thenReturn(false);
        when(translator.translate(eq(TranslationKey.GAME_REMOVAL_FAILED.getPath()), any(PlaceholderEntry.class))).thenReturn(message);

        RemoveGameCommand command = new RemoveGameCommand(contextProvider, taskRunner, translator);
        command.execute(sender, gameId);
        command.execute(sender, gameId);

        verify(sender).sendMessage(message);
    }
}
