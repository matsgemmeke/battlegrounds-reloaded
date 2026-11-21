package nl.matsgemmeke.battlegrounds.arena.command.executor;

import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.i18n.*;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RemoveArenaCommandExecutorTest {

    private static final int ARENA_ID = 1;
    private static final GameKey GAME_KEY = GameKey.ofArena(ARENA_ID);

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
        Message arenaConfirmRemovalMessage = mock(Message.class);

        when(translator.translateMessage(eq(TranslationKey.ARENA_CONFIRM_REMOVAL.getPath()), any(Replacement[].class))).thenReturn(Optional.of(arenaConfirmRemovalMessage));

        commandExecutor.execute(sender, ARENA_ID);

        ArgumentCaptor<Replacement[]> replacementsCaptor = ArgumentCaptor.forClass(Replacement[].class);
        verify(translator).translateMessage(eq(TranslationKey.ARENA_CONFIRM_REMOVAL.getPath()), replacementsCaptor.capture());

        assertThat(replacementsCaptor.getValue()).satisfiesExactly(replacement -> {
            assertThat(replacement.key()).isEqualTo("arena-id");
            assertThat(replacement.value()).isEqualTo("1");
        });

        verify(arenaConfirmRemovalMessage).send(sender);
        verify(scheduler).createSingleRunSchedule(200L);
    }

    @Test
    @DisplayName("execute notifies command sender when failing to remove arena")
    void execute_arenaRemovalFailed() {
        Message arenaRemovalFailedMessage = mock(Message.class);

        when(gameContextProvider.removeGameContext(GAME_KEY)).thenReturn(false);
        when(translator.translateMessage(eq(TranslationKey.ARENA_CONFIRM_REMOVAL.getPath()), any(Replacement[].class))).thenReturn(Optional.empty());
        when(translator.translateMessage(eq(TranslationKey.ARENA_REMOVAL_FAILED.getPath()), any(Replacement[].class))).thenReturn(Optional.of(arenaRemovalFailedMessage));

        commandExecutor.execute(sender, ARENA_ID);
        commandExecutor.execute(sender, ARENA_ID);

        ArgumentCaptor<Replacement[]> replacementsCaptor = ArgumentCaptor.forClass(Replacement[].class);
        verify(translator).translateMessage(eq(TranslationKey.ARENA_REMOVAL_FAILED.getPath()), replacementsCaptor.capture());

        assertThat(replacementsCaptor.getValue()).satisfiesExactly(replacement -> {
            assertThat(replacement.key()).isEqualTo("arena-id");
            assertThat(replacement.value()).isEqualTo("1");
        });

        verify(arenaRemovalFailedMessage).send(sender);
    }

    @Test
    @DisplayName("execute removes arena and notifies command sender")
    void execute_successfulArenaRemoval() {
        File arenaFolder = new File(arenasFolder, "arena-" + ARENA_ID);
        arenaFolder.mkdirs();

        Message arenaRemovedMessage = mock(Message.class);

        when(gameContextProvider.removeGameContext(GAME_KEY)).thenReturn(true);
        when(translator.translateMessage(eq(TranslationKey.ARENA_CONFIRM_REMOVAL.getPath()), any(Replacement[].class))).thenReturn(Optional.empty());
        when(translator.translateMessage(eq(TranslationKey.ARENA_REMOVED.getPath()), any(Replacement[].class))).thenReturn(Optional.of(arenaRemovedMessage));

        commandExecutor.execute(sender, ARENA_ID);
        commandExecutor.execute(sender, ARENA_ID);

        ArgumentCaptor<Replacement[]> replacementsCaptor = ArgumentCaptor.forClass(Replacement[].class);
        verify(translator).translateMessage(eq(TranslationKey.ARENA_REMOVED.getPath()), replacementsCaptor.capture());

        assertThat(replacementsCaptor.getValue()).satisfiesExactly(replacement -> {
            assertThat(replacement.key()).isEqualTo("arena-id");
            assertThat(replacement.value()).isEqualTo("1");
        });

        assertThat(arenaFolder).doesNotExist();

        verify(arenaRemovedMessage).send(sender);
    }
}
