package nl.matsgemmeke.battlegrounds.item.reload.magazine;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.reload.AmmunitionStorage;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadPerformer;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadProperties;
import nl.matsgemmeke.battlegrounds.util.Procedure;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MagazineReloadSystemTest {

    private static final List<GameSound> RELOAD_SOUNDS = Collections.emptyList();
    private static final long DURATION = 50L;

    private AudioEmitter audioEmitter;
    private AmmunitionStorage ammunitionStorage;
    private ReloadProperties properties;
    private TaskRunner taskRunner;

    @BeforeEach
    public void setUp() {
        ammunitionStorage = new AmmunitionStorage(30, 30, 90, 300);
        audioEmitter = mock(AudioEmitter.class);
        properties = new ReloadProperties(RELOAD_SOUNDS, DURATION);
        taskRunner = mock(TaskRunner.class);
    }

    @Test
    public void isPerformingReturnsFalseIfReloadWasNotActivated() {
        MagazineReloadSystem reloadSystem = new MagazineReloadSystem(taskRunner, properties, ammunitionStorage, audioEmitter);
        boolean performing = reloadSystem.isPerforming();

        assertFalse(performing);
    }

    @Test
    public void isPerformingReturnsTrueIfReloadWasActivated() {
        ReloadPerformer performer = mock(ReloadPerformer.class);

        MagazineReloadSystem reloadSystem = new MagazineReloadSystem(taskRunner, properties, ammunitionStorage, audioEmitter);
        reloadSystem.performReload(performer, () -> {});
        boolean performing = reloadSystem.isPerforming();

        assertTrue(performing);
    }

    @Test
    public void performReloadSchedulesDelayedTaskAndRefillsMagazineWhenEnoughAmmoIsAvailable() {
        Location performerLocation = new Location(null, 1, 1, 1);
        long soundDelay = 20L;
        Procedure callback = mock(Procedure.class);

        GameSound sound = mock(GameSound.class);
        when(sound.getDelay()).thenReturn(soundDelay);

        ReloadPerformer performer = mock(ReloadPerformer.class);
        when(performer.getAudioPlayLocation()).thenReturn(performerLocation);

        ReloadProperties properties = new ReloadProperties(List.of(sound), DURATION);

        ammunitionStorage.setMagazineAmmo(0);

        MagazineReloadSystem reloadSystem = new MagazineReloadSystem(taskRunner, properties, ammunitionStorage, audioEmitter);
        boolean performed = reloadSystem.performReload(performer, callback);

        ArgumentCaptor<Runnable> soundRunnable = ArgumentCaptor.forClass(Runnable.class);
        ArgumentCaptor<Runnable> reloadRunnable = ArgumentCaptor.forClass(Runnable.class);

        verify(taskRunner).runTaskLater(soundRunnable.capture(), eq(soundDelay));
        verify(taskRunner).runTaskLater(reloadRunnable.capture(), eq(DURATION));

        soundRunnable.getValue().run();
        reloadRunnable.getValue().run();

        assertTrue(performed);
        assertEquals(30, ammunitionStorage.getMagazineAmmo());
        assertEquals(60, ammunitionStorage.getReserveAmmo());

        verify(audioEmitter).playSound(sound, performerLocation);
        verify(callback).apply();
        verify(performer).applyReloadingState();
        verify(performer).resetReloadingState();
    }

    @Test
    public void performReloadSchedulesDelayedTaskAndPartiallyFillsMagazineWhenNotEnoughAmmoIsAvailable() {
        ReloadPerformer performer = mock(ReloadPerformer.class);
        Procedure callback = mock(Procedure.class);

        ammunitionStorage.setMagazineAmmo(0);
        ammunitionStorage.setReserveAmmo(10);

        MagazineReloadSystem reloadSystem = new MagazineReloadSystem(taskRunner, properties, ammunitionStorage, audioEmitter);
        boolean performed = reloadSystem.performReload(performer, callback);

        ArgumentCaptor<Runnable> reloadRunnable = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskLater(reloadRunnable.capture(), eq(DURATION));

        reloadRunnable.getValue().run();

        assertTrue(performed);
        assertEquals(10, ammunitionStorage.getMagazineAmmo());
        assertEquals(0, ammunitionStorage.getReserveAmmo());

        verify(audioEmitter, never()).playSound(any(GameSound.class), any(Location.class));
        verify(callback).apply();
        verify(performer).applyReloadingState();
        verify(performer).resetReloadingState();
    }

    @Test
    public void cancelReloadDoesNotCancelIfItHasNoPerformer() {
        MagazineReloadSystem reloadSystem = new MagazineReloadSystem(taskRunner, properties, ammunitionStorage, audioEmitter);
        boolean cancelled = reloadSystem.cancelReload();

        assertFalse(cancelled);
    }

    @Test
    public void cancelReloadResetsPerformerStateAndCancelsRunningTasks() {
        BukkitTask task = mock(BukkitTask.class);
        ReloadPerformer performer = mock(ReloadPerformer.class);

        when(taskRunner.runTaskLater(any(Runnable.class), anyLong())).thenReturn(task);

        MagazineReloadSystem reloadSystem = new MagazineReloadSystem(taskRunner, properties, ammunitionStorage, audioEmitter);
        reloadSystem.performReload(performer, () -> {});

        boolean cancelled = reloadSystem.cancelReload();

        assertTrue(cancelled);

        verify(performer).resetReloadingState();
        verify(task).cancel();
    }
}
