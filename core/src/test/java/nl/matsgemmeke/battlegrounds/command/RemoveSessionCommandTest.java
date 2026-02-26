package nl.matsgemmeke.battlegrounds.command;

import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import nl.matsgemmeke.battlegrounds.text.TextTemplate;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RemoveSessionCommandTest {

    private static final int SESSION_ID = 1;
    private static final String MESSAGE = "hello";

    @Mock
    private CommandSender sender;
    @Mock
    private GameContextProvider gameContextProvider;
    @Mock
    private Schedule schedule;
    @Mock
    private Scheduler scheduler;
    @Mock
    private Translator translator;

    private RemoveSessionCommand command;

    @BeforeEach
    void setUp() {
        when(scheduler.createSingleRunSchedule(200L)).thenReturn(schedule);
        when(translator.translate(TranslationKey.DESCRIPTION_REMOVESESSION.getPath())).thenReturn(new TextTemplate("description"));

        command = new RemoveSessionCommand(gameContextProvider, scheduler, translator);
    }

    @Test
    @DisplayName("execute adds command sender to confirm list upon first executing the command")
    void execute_firstExecution() {
        when(translator.translate(TranslationKey.SESSION_CONFIRM_REMOVAL.getPath())).thenReturn(new TextTemplate(MESSAGE));

        command.execute(sender, SESSION_ID);

        verify(sender).sendMessage(MESSAGE);
        verify(scheduler).createSingleRunSchedule(200L);
    }

    @Test
    @DisplayName("execute notifies command sender when failing to remove session")
    void execute_sessionRemovalFailed() {
        when(gameContextProvider.removeSession(SESSION_ID)).thenReturn(false);
        when(translator.translate(TranslationKey.SESSION_CONFIRM_REMOVAL.getPath())).thenReturn(new TextTemplate("test"));
        when(translator.translate(TranslationKey.SESSION_REMOVAL_FAILED.getPath())).thenReturn(new TextTemplate(MESSAGE));

        command.execute(sender, SESSION_ID);
        command.execute(sender, SESSION_ID);

        verify(sender).sendMessage(MESSAGE);
    }

    @Test
    @DisplayName("execute removes session and notifies command sender")
    void execute_successfulSessionRemoval() {
        when(gameContextProvider.removeSession(SESSION_ID)).thenReturn(true);
        when(translator.translate(TranslationKey.SESSION_CONFIRM_REMOVAL.getPath())).thenReturn(new TextTemplate("test"));
        when(translator.translate(TranslationKey.SESSION_REMOVED.getPath())).thenReturn(new TextTemplate(MESSAGE));

        command.execute(sender, SESSION_ID);
        command.execute(sender, SESSION_ID);

        verify(sender).sendMessage(MESSAGE);
    }
}
