package nl.matsgemmeke.battlegrounds.item.reload.magazine;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.AmmunitionHolder;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadPerformer;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadProperties;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class MagazineReloadSystemTest {

    private static final List<GameSound> RELOAD_SOUNDS = Collections.emptyList();
    private static final long DURATION = 50L;

    private AudioEmitter audioEmitter;
    private AmmunitionHolder ammunitionHolder;
    private ReloadProperties properties;
    private TaskRunner taskRunner;

    @BeforeEach
    public void setUp() {
        ammunitionHolder = mock(AmmunitionHolder.class);
        audioEmitter = mock(AudioEmitter.class);
        properties = new ReloadProperties(RELOAD_SOUNDS, DURATION);
        taskRunner = mock(TaskRunner.class);
    }

    @Test
    public void isPerformingReturnsFalseIfReloadWasNotActivated() {
        MagazineReloadSystem reloadSystem = new MagazineReloadSystem(taskRunner, properties, ammunitionHolder, audioEmitter);
        boolean performing = reloadSystem.isPerforming();

        assertFalse(performing);
    }

    @Test
    public void isPerformingReturnsTrueIfReloadWasActivated() {
        ReloadPerformer performer = mock(ReloadPerformer.class);

        MagazineReloadSystem reloadSystem = new MagazineReloadSystem(taskRunner, properties, ammunitionHolder, audioEmitter);
        reloadSystem.performReload(performer);
        boolean performing = reloadSystem.isPerforming();

        assertTrue(performing);
    }

    @Test
    public void performReloadSchedulesDelayedTaskAndRefillsMagazineWhenEnoughAmmoIsAvailable() {
        Location performerLocation = new Location(null, 1, 1, 1);
        long soundDelay = 20L;

        GameSound sound = mock(GameSound.class);
        when(sound.getDelay()).thenReturn(soundDelay);

        ReloadPerformer performer = mock(ReloadPerformer.class);
        when(performer.getAudioPlayLocation()).thenReturn(performerLocation);

        ReloadProperties properties = new ReloadProperties(List.of(sound), DURATION);

        when(ammunitionHolder.getMagazineAmmo()).thenReturn(0);
        when(ammunitionHolder.getMagazineSize()).thenReturn(30);
        when(ammunitionHolder.getReserveAmmo()).thenReturn(90);

        MagazineReloadSystem reloadSystem = new MagazineReloadSystem(taskRunner, properties, ammunitionHolder, audioEmitter);
        boolean activated = reloadSystem.performReload(performer);

        ArgumentCaptor<Runnable> soundRunnable = ArgumentCaptor.forClass(Runnable.class);
        ArgumentCaptor<Runnable> reloadRunnable = ArgumentCaptor.forClass(Runnable.class);

        verify(taskRunner).runTaskLater(soundRunnable.capture(), eq(soundDelay));
        verify(taskRunner).runTaskLater(reloadRunnable.capture(), eq(DURATION));

        soundRunnable.getValue().run();
        reloadRunnable.getValue().run();

        assertTrue(activated);

        verify(ammunitionHolder).setMagazineAmmo(30);
        verify(ammunitionHolder).setReserveAmmo(60);
        verify(audioEmitter).playSound(sound, performerLocation);
        verify(performer).applyReloadingState();
        verify(performer).resetReloadingState();
    }

    @Test
    public void performReloadSchedulesDelayedTaskAndPartiallyFillsMagazineWhenNotEnoughAmmoIsAvailable() {
        ReloadPerformer performer = mock(ReloadPerformer.class);

        when(ammunitionHolder.getMagazineAmmo()).thenReturn(0);
        when(ammunitionHolder.getMagazineSize()).thenReturn(30);
        when(ammunitionHolder.getReserveAmmo()).thenReturn(10);

        MagazineReloadSystem reloadSystem = new MagazineReloadSystem(taskRunner, properties, ammunitionHolder, audioEmitter);
        boolean activated = reloadSystem.performReload(performer);

        ArgumentCaptor<Runnable> reloadRunnable = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskLater(reloadRunnable.capture(), eq(DURATION));

        reloadRunnable.getValue().run();

        assertTrue(activated);

        verify(ammunitionHolder).setMagazineAmmo(10);
        verify(ammunitionHolder).setReserveAmmo(0);
        verify(audioEmitter, never()).playSound(any(GameSound.class), any(Location.class));
        verify(performer).applyReloadingState();
        verify(performer).resetReloadingState();
    }

    @Test
    public void performReloadSchedulesDelayedTaskForPlayingSounds() {
        long soundDelay = 20;

        GameSound sound = mock(GameSound.class);
        when(sound.getDelay()).thenReturn(soundDelay);

        ReloadPerformer performer = mock(ReloadPerformer.class);
        ReloadProperties properties = new ReloadProperties(List.of(sound), DURATION);

        MagazineReloadSystem reloadSystem = new MagazineReloadSystem(taskRunner, properties, ammunitionHolder, audioEmitter);
        boolean activated = reloadSystem.performReload(performer);

        assertTrue(activated);

        verify(taskRunner).runTaskLater(any(Runnable.class), eq(soundDelay));
    }

    @Test
    public void cancelReloadDoesNotCancelIfItHasNoPerformer() {
        MagazineReloadSystem reloadSystem = new MagazineReloadSystem(taskRunner, properties, ammunitionHolder, audioEmitter);
        boolean cancelled = reloadSystem.cancelReload();

        assertFalse(cancelled);
    }

    @Test
    public void cancelReloadResetsPerformerStateAndCancelsRunningTasks() {
        BukkitTask task = mock(BukkitTask.class);
        ReloadPerformer performer = mock(ReloadPerformer.class);

        when(taskRunner.runTaskLater(any(Runnable.class), anyLong())).thenReturn(task);

        MagazineReloadSystem reloadSystem = new MagazineReloadSystem(taskRunner, properties, ammunitionHolder, audioEmitter);
        reloadSystem.performReload(performer);

        boolean cancelled = reloadSystem.cancelReload();

        assertTrue(cancelled);

        verify(performer).resetReloadingState();
        verify(task).cancel();
    }
}
