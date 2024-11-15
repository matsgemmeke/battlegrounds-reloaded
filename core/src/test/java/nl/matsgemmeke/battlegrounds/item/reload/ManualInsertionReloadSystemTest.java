package nl.matsgemmeke.battlegrounds.item.reload;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.AmmunitionHolder;
import org.bukkit.scheduler.BukkitTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class ManualInsertionReloadSystemTest {

    private AmmunitionHolder ammunitionHolder;
    private AudioEmitter audioEmitter;
    private TaskRunner taskRunner;

    @BeforeEach
    public void setUp() {
        ammunitionHolder = mock(AmmunitionHolder.class);
        audioEmitter = mock(AudioEmitter.class);
        taskRunner = mock(TaskRunner.class);
    }

    @Test
    public void shouldReturnNotPerformingIfReloadWasNotActivated() {
        ManualInsertionReloadSystem reloadSystem = new ManualInsertionReloadSystem(ammunitionHolder, audioEmitter, taskRunner, 0);
        boolean performing = reloadSystem.isPerforming();

        assertFalse(performing);
    }

    @Test
    public void shouldReturnPerformingIfReloadWasActivated() {
        ReloadPerformer performer = mock(ReloadPerformer.class);

        ManualInsertionReloadSystem reloadSystem = new ManualInsertionReloadSystem(ammunitionHolder, audioEmitter, taskRunner, 0);
        reloadSystem.performReload(performer);
        boolean performing = reloadSystem.isPerforming();

        assertTrue(performing);
    }

    @Test
    public void shouldScheduleRepeatingTaskForAddingAmmunitionWhenPerformingReload() {
        long duration = 10;

        ReloadPerformer performer = mock(ReloadPerformer.class);

        ManualInsertionReloadSystem reloadSystem = new ManualInsertionReloadSystem(ammunitionHolder, audioEmitter, taskRunner, duration);
        boolean activated = reloadSystem.performReload(performer);

        assertTrue(activated);

        verify(taskRunner).runTaskTimer(any(Runnable.class), eq(duration), eq(duration));
    }

    @Test
    public void shouldScheduledRepeatingTaskForPlayingSoundsWhenPerformingReload() {
        long duration = 10;
        long soundDelay = 20;

        GameSound sound = mock(GameSound.class);
        when(sound.getDelay()).thenReturn(soundDelay);

        ReloadPerformer performer = mock(ReloadPerformer.class);

        Iterable<GameSound> reloadSounds = Collections.singletonList(sound);

        ManualInsertionReloadSystem reloadSystem = new ManualInsertionReloadSystem(ammunitionHolder, audioEmitter, taskRunner, duration);
        reloadSystem.setReloadSounds(reloadSounds);

        boolean activated = reloadSystem.performReload(performer);

        assertTrue(activated);

        verify(taskRunner).runTaskTimer(any(Runnable.class), eq(soundDelay), eq(duration));
    }

    @Test
    public void shouldNotCancelIfItHasNoPerformer() {
        ManualInsertionReloadSystem reloadSystem = new ManualInsertionReloadSystem(ammunitionHolder, audioEmitter, taskRunner, 0);
        boolean cancelled = reloadSystem.cancelReload();

        assertFalse(cancelled);
    }

    @Test
    public void shouldResetPerformerStateWhenCancelling() {
        ReloadPerformer performer = mock(ReloadPerformer.class);

        when(taskRunner.runTaskTimer(any(Runnable.class), anyLong(), anyLong())).thenReturn(mock(BukkitTask.class));

        ManualInsertionReloadSystem reloadSystem = new ManualInsertionReloadSystem(ammunitionHolder, audioEmitter, taskRunner, 0);
        reloadSystem.performReload(performer);

        boolean cancelled = reloadSystem.cancelReload();

        assertTrue(cancelled);

        verify(performer).resetReloadingState();
    }

    @Test
    public void shouldCancelBukkitTasksWhenCancelling() {
        BukkitTask task = mock(BukkitTask.class);
        ReloadPerformer performer = mock(ReloadPerformer.class);

        when(taskRunner.runTaskTimer(any(Runnable.class), anyLong(), anyLong())).thenReturn(task);

        ManualInsertionReloadSystem reloadSystem = new ManualInsertionReloadSystem(ammunitionHolder, audioEmitter, taskRunner, 0);
        reloadSystem.performReload(performer);

        boolean cancelled = reloadSystem.cancelReload();

        assertTrue(cancelled);

        verify(task).cancel();
    }

    @Test
    public void shouldAddSingleAmmoWithEachIteration() {
        when(ammunitionHolder.getReserveAmmo()).thenReturn(1);

        ManualInsertionReloadSystem reloadSystem = new ManualInsertionReloadSystem(ammunitionHolder, audioEmitter, taskRunner, 0);
        reloadSystem.addAmmunition();

        verify(ammunitionHolder).setMagazineAmmo(1);
        verify(ammunitionHolder).setReserveAmmo(0);
    }
}
