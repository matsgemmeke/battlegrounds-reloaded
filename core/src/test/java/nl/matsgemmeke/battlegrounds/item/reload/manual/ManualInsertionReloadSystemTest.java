package nl.matsgemmeke.battlegrounds.item.reload.manual;

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

public class ManualInsertionReloadSystemTest {

    private static final List<GameSound> RELOAD_SOUNDS = Collections.emptyList();
    private static final long DURATION = 50L;

    private AmmunitionStorage ammunitionStorage;
    private AudioEmitter audioEmitter;
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
        ManualInsertionReloadSystem reloadSystem = new ManualInsertionReloadSystem(taskRunner, properties, ammunitionStorage, audioEmitter);
        boolean performing = reloadSystem.isPerforming();

        assertFalse(performing);
    }

    @Test
    public void isPerformingReturnsTrueIfReloadWasActivated() {
        ReloadPerformer performer = mock(ReloadPerformer.class);

        ManualInsertionReloadSystem reloadSystem = new ManualInsertionReloadSystem(taskRunner, properties, ammunitionStorage, audioEmitter);
        reloadSystem.performReload(performer, () -> {});
        boolean performing = reloadSystem.isPerforming();

        assertTrue(performing);
    }

    @Test
    public void performReloadSchedulesRepeatingTaskAndAddsAmmunition() {
        BukkitTask reloadTask = mock(BukkitTask.class);
        BukkitTask soundTask = mock(BukkitTask.class);
        Location performerLocation = new Location(null, 1, 1, 1);
        long soundDelay = 20;
        Procedure callback = mock(Procedure.class);

        GameSound sound = mock(GameSound.class);
        when(sound.getDelay()).thenReturn(soundDelay);

        ReloadPerformer performer = mock(ReloadPerformer.class);
        when(performer.getAudioPlayLocation()).thenReturn(performerLocation);

        ReloadProperties properties = new ReloadProperties(List.of(sound), DURATION);

        ammunitionStorage.setMagazineAmmo(0);
        ammunitionStorage.setReserveAmmo(1);

        when(taskRunner.runTaskTimer(any(Runnable.class), eq(DURATION), eq(DURATION))).thenReturn(reloadTask);
        when(taskRunner.runTaskTimer(any(Runnable.class), eq(soundDelay), eq(DURATION))).thenReturn(soundTask);

        ManualInsertionReloadSystem reloadSystem = new ManualInsertionReloadSystem(taskRunner, properties, ammunitionStorage, audioEmitter);
        boolean activated = reloadSystem.performReload(performer, callback);

        ArgumentCaptor<Runnable> soundRunnable = ArgumentCaptor.forClass(Runnable.class);
        ArgumentCaptor<Runnable> reloadRunnable = ArgumentCaptor.forClass(Runnable.class);

        verify(taskRunner).runTaskTimer(soundRunnable.capture(), eq(soundDelay), eq(DURATION));
        verify(taskRunner).runTaskTimer(reloadRunnable.capture(), eq(DURATION), eq(DURATION));

        soundRunnable.getValue().run();
        reloadRunnable.getValue().run();

        assertTrue(activated);
        assertEquals(1, ammunitionStorage.getMagazineAmmo());
        assertEquals(0, ammunitionStorage.getReserveAmmo());

        verify(audioEmitter).playSound(sound, performerLocation);
        verify(callback).apply();
        verify(performer).applyReloadingState();
        verify(performer).resetReloadingState();
        verify(reloadTask).cancel();
        verify(soundTask).cancel();
    }

    @Test
    public void cancelReloadDoesNotCancelIfItHasNoPerformer() {
        ManualInsertionReloadSystem reloadSystem = new ManualInsertionReloadSystem(taskRunner, properties, ammunitionStorage, audioEmitter);
        boolean cancelled = reloadSystem.cancelReload();

        assertFalse(cancelled);
    }

    @Test
    public void cancelReloadResetsPerformerStateAndCancelBukkitTasks() {
        BukkitTask task = mock(BukkitTask.class);
        ReloadPerformer performer = mock(ReloadPerformer.class);

        when(taskRunner.runTaskTimer(any(Runnable.class), anyLong(), anyLong())).thenReturn(task);

        ManualInsertionReloadSystem reloadSystem = new ManualInsertionReloadSystem(taskRunner, properties, ammunitionStorage, audioEmitter);
        reloadSystem.performReload(performer, () -> {});
        boolean cancelled = reloadSystem.cancelReload();

        assertTrue(cancelled);

        verify(performer).resetReloadingState();
        verify(task).cancel();
    }
}
