package nl.matsgemmeke.battlegrounds.item.reload;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.AmmunitionHolder;
import org.bukkit.scheduler.BukkitTask;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MagazineReloadSystemTest {

    private AudioEmitter audioEmitter;
    private AmmunitionHolder ammunitionHolder;
    private TaskRunner taskRunner;

    @Before
    public void setUp() {
        ammunitionHolder = mock(AmmunitionHolder.class);
        audioEmitter = mock(AudioEmitter.class);
        taskRunner = mock(TaskRunner.class);
    }

    @Test
    public void shouldReturnNotPerformingIfReloadWasNotActivated() {
        MagazineReloadSystem reloadSystem = new MagazineReloadSystem(ammunitionHolder, audioEmitter, taskRunner, 0);
        boolean performing = reloadSystem.isPerforming();

        assertFalse(performing);
    }

    @Test
    public void shouldReturnPerformingIfReloadWasActivated() {
        ReloadPerformer performer = mock(ReloadPerformer.class);

        MagazineReloadSystem reloadSystem = new MagazineReloadSystem(ammunitionHolder, audioEmitter, taskRunner, 0);
        reloadSystem.performReload(performer);
        boolean performing = reloadSystem.isPerforming();

        assertTrue(performing);
    }

    @Test
    public void shouldScheduleDelayedTaskForAddingAmmunitionWhenPerformingReload() {
        long duration = 10;

        ReloadPerformer performer = mock(ReloadPerformer.class);

        MagazineReloadSystem reloadSystem = new MagazineReloadSystem(ammunitionHolder, audioEmitter, taskRunner, duration);
        boolean activated = reloadSystem.performReload(performer);

        assertTrue(activated);

        verify(taskRunner).runTaskLater(any(Runnable.class), eq(duration));
    }

    @Test
    public void shouldScheduledDelayedTaskForPlayingSoundsWhenPerformingReload() {
        long duration = 10;
        long soundDelay = 20;

        GameSound sound = mock(GameSound.class);
        when(sound.getDelay()).thenReturn(soundDelay);

        ReloadPerformer performer = mock(ReloadPerformer.class);

        Iterable<GameSound> reloadSounds = Collections.singletonList(sound);

        MagazineReloadSystem reloadSystem = new MagazineReloadSystem(ammunitionHolder, audioEmitter, taskRunner, duration);
        reloadSystem.setReloadSounds(reloadSounds);

        boolean activated = reloadSystem.performReload(performer);

        assertTrue(activated);

        verify(taskRunner).runTaskLater(any(Runnable.class), eq(soundDelay));
    }

    @Test
    public void shouldNotCancelIfItHasNoPerformer() {
        MagazineReloadSystem reloadSystem = new MagazineReloadSystem(ammunitionHolder, audioEmitter, taskRunner, 0);
        boolean cancelled = reloadSystem.cancel();

        assertFalse(cancelled);
    }

    @Test
    public void shouldResetPerformerStateWhenCancelling() {
        ReloadPerformer performer = mock(ReloadPerformer.class);

        when(taskRunner.runTaskLater(any(Runnable.class), anyLong())).thenReturn(mock(BukkitTask.class));

        MagazineReloadSystem reloadSystem = new MagazineReloadSystem(ammunitionHolder, audioEmitter, taskRunner, 0);
        reloadSystem.performReload(performer);

        boolean cancelled = reloadSystem.cancel();

        assertTrue(cancelled);

        verify(performer).resetReloadingState();
    }

    @Test
    public void shouldCancelBukkitTasksWhenCancelling() {
        BukkitTask task = mock(BukkitTask.class);
        ReloadPerformer performer = mock(ReloadPerformer.class);

        when(taskRunner.runTaskLater(any(Runnable.class), anyLong())).thenReturn(task);

        MagazineReloadSystem reloadSystem = new MagazineReloadSystem(ammunitionHolder, audioEmitter, taskRunner, 0);
        reloadSystem.performReload(performer);

        boolean cancelled = reloadSystem.cancel();

        assertTrue(cancelled);

        verify(task).cancel();
    }

    @Test
    public void refillMagazineCompletelyWhenEnoughReserveAmmo() {
        ReloadPerformer performer = mock(ReloadPerformer.class);

        when(ammunitionHolder.getMagazineAmmo()).thenReturn(0);
        when(ammunitionHolder.getMagazineSize()).thenReturn(30);
        when(ammunitionHolder.getReserveAmmo()).thenReturn(90);

        MagazineReloadSystem reloadSystem = new MagazineReloadSystem(ammunitionHolder, audioEmitter, taskRunner, 0);
        reloadSystem.finalizeReload(performer);

        verify(ammunitionHolder).setMagazineAmmo(30);
        verify(ammunitionHolder).setReserveAmmo(60);
    }

    @Test
    public void refillMagazinePartiallyWhenNotEnoughReserveAmmo() {
        ReloadPerformer performer = mock(ReloadPerformer.class);

        when(ammunitionHolder.getMagazineAmmo()).thenReturn(0);
        when(ammunitionHolder.getMagazineSize()).thenReturn(30);
        when(ammunitionHolder.getReserveAmmo()).thenReturn(10);

        MagazineReloadSystem reloadSystem = new MagazineReloadSystem(ammunitionHolder, audioEmitter, taskRunner, 0);
        reloadSystem.finalizeReload(performer);

        verify(ammunitionHolder).setMagazineAmmo(10);
        verify(ammunitionHolder).setReserveAmmo(0);
    }
}
