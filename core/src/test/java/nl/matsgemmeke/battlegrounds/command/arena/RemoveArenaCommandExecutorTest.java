package nl.matsgemmeke.battlegrounds.command.arena;

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
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RemoveArenaCommandExecutorTest {

    private static final int ARENA_ID = 1;
    private static final String MESSAGE = "hello";

    @TempDir
    private static File tempDir;
    @Mock
    private CommandSender sender;
    @Spy
    private File arenasFolder = new File(tempDir, "/arenas");
    @Mock
    private GameContextProvider gameContextProvider;
    @Mock
    private Schedule schedule;
    @Mock
    private Scheduler scheduler;
    @Mock
    private Translator translator;
    @InjectMocks
    private RemoveArenaCommandExecutor commandExecutor;

    @BeforeEach
    void setUp() {
        when(scheduler.createSingleRunSchedule(200L)).thenReturn(schedule);

        arenasFolder.mkdirs();
    }

    @Test
    @DisplayName("execute adds command sender to confirm list upon first executing the command")
    void execute_firstExecution() {
        when(translator.translate(TranslationKey.ARENA_CONFIRM_REMOVAL.getPath())).thenReturn(new TextTemplate(MESSAGE));

        commandExecutor.execute(sender, ARENA_ID);

        verify(sender).sendMessage(MESSAGE);
        verify(scheduler).createSingleRunSchedule(200L);
    }

    @Test
    @DisplayName("execute notifies command sender when failing to remove arena")
    void execute_arenaRemovalFailed() {
        when(gameContextProvider.removeArena(ARENA_ID)).thenReturn(false);
        when(translator.translate(TranslationKey.ARENA_CONFIRM_REMOVAL.getPath())).thenReturn(new TextTemplate("test"));
        when(translator.translate(TranslationKey.ARENA_REMOVAL_FAILED.getPath())).thenReturn(new TextTemplate(MESSAGE));

        commandExecutor.execute(sender, ARENA_ID);
        commandExecutor.execute(sender, ARENA_ID);

        verify(sender).sendMessage(MESSAGE);
    }

    @Test
    @DisplayName("execute removes arena and notifies command sender")
    void execute_successfulArenaRemoval() {
        File arenaFolder = new File(arenasFolder, "arena-" + ARENA_ID);
        arenaFolder.mkdirs();

        when(gameContextProvider.removeArena(ARENA_ID)).thenReturn(true);
        when(translator.translate(TranslationKey.ARENA_CONFIRM_REMOVAL.getPath())).thenReturn(new TextTemplate("test"));
        when(translator.translate(TranslationKey.ARENA_REMOVED.getPath())).thenReturn(new TextTemplate(MESSAGE));

        commandExecutor.execute(sender, ARENA_ID);
        commandExecutor.execute(sender, ARENA_ID);

        assertThat(arenaFolder).doesNotExist();

        verify(sender).sendMessage(MESSAGE);
    }
}
