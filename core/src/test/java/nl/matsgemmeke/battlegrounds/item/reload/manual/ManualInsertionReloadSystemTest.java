package nl.matsgemmeke.battlegrounds.item.reload.manual;

import nl.matsgemmeke.battlegrounds.MockUtils;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadPerformer;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadProperties;
import nl.matsgemmeke.battlegrounds.item.reload.ResourceContainer;
import nl.matsgemmeke.battlegrounds.item.reload.magazine.MagazineReloadSystem;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.ScheduleTask;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import nl.matsgemmeke.battlegrounds.sound.SoundEmitter;
import nl.matsgemmeke.battlegrounds.sound.SoundPlayback;
import nl.matsgemmeke.battlegrounds.sound.SoundSequence;
import nl.matsgemmeke.battlegrounds.util.Procedure;
import org.bukkit.Location;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ManualInsertionReloadSystemTest {

    private static final SoundSequence SOUND_SEQUENCE = new SoundSequence(null, null);
    private static final long DURATION = 50L;

    private static final ReloadProperties PROPERTIES_DEFAULT = new ReloadProperties(SOUND_SEQUENCE, DURATION);
    private static final ReloadProperties PROPERTIES_NO_SOUND_SEQUENCE = new ReloadProperties(null, DURATION);

    @Mock
    private Scheduler scheduler;
    @Mock
    private SoundEmitter soundEmitter;

    @Test
    @DisplayName("isPerforming returns false when reload is not activated")
    void isPerforming_notReloading() {
        ResourceContainer resourceContainer = new ResourceContainer(30, 30, 90, 300);

        MagazineReloadSystem reloadSystem = new MagazineReloadSystem(scheduler, soundEmitter, PROPERTIES_DEFAULT, resourceContainer);
        boolean performing = reloadSystem.isPerforming();

        assertThat(performing).isFalse();
    }

    @Test
    @DisplayName("isPerforming returns true when reload is activated")
    void isPerforming_reloading() {
        ResourceContainer resourceContainer = new ResourceContainer(30, 30, 90, 300);
        Schedule soundPlaySchedule = mock(Schedule.class);
        Schedule reloadFinishSchedule = mock(Schedule.class);
        ReloadPerformer performer = mock(ReloadPerformer.class);

        when(scheduler.createRepeatingSchedule(0, DURATION)).thenReturn(soundPlaySchedule);
        when(scheduler.createRepeatingSchedule(DURATION, DURATION)).thenReturn(reloadFinishSchedule);

        ManualInsertionReloadSystem reloadSystem = new ManualInsertionReloadSystem(scheduler, soundEmitter, PROPERTIES_DEFAULT, resourceContainer);
        reloadSystem.performReload(performer, () -> {});
        boolean performing = reloadSystem.isPerforming();

        assertThat(performing).isTrue();
    }

    @Test
    @DisplayName("performReload creates repeating schedules that play sounds and add loaded resources")
    void performReload_createsRepeatingSchedules() {
        ResourceContainer resourceContainer = new ResourceContainer(30, 0, 1, 300);
        Location performerLocation = new Location(null, 1, 1, 1);
        SoundPlayback soundPlayback = mock(SoundPlayback.class);
        Procedure callback = mock(Procedure.class);

        Schedule soundPlaySchedule = mock(Schedule.class);
        doAnswer(MockUtils.answerRunScheduleTask()).when(soundPlaySchedule).addTask(any(ScheduleTask.class));

        Schedule reloadFinishSchedule = mock(Schedule.class);
        doAnswer(MockUtils.answerRunScheduleTask()).when(reloadFinishSchedule).addTask(any(ScheduleTask.class));

        ReloadPerformer performer = mock(ReloadPerformer.class);
        when(performer.getLocation()).thenReturn(performerLocation);

        when(scheduler.createRepeatingSchedule(0, DURATION)).thenReturn(soundPlaySchedule);
        when(soundEmitter.playSoundSequence(eq(SOUND_SEQUENCE), Mockito.<Supplier<Location>>argThat(supplier -> supplier.get() == performerLocation))).thenReturn(soundPlayback);
        when(scheduler.createRepeatingSchedule(DURATION, DURATION)).thenReturn(reloadFinishSchedule);

        ManualInsertionReloadSystem reloadSystem = new ManualInsertionReloadSystem(scheduler, soundEmitter, PROPERTIES_DEFAULT, resourceContainer);
        boolean activated = reloadSystem.performReload(performer, callback);

        assertThat(activated).isTrue();
        assertThat(resourceContainer.getLoadedAmount()).isOne();
        assertThat(resourceContainer.getReserveAmount()).isZero();

        verify(soundEmitter).playSoundSequence(eq(SOUND_SEQUENCE), Mockito.<Supplier<Location>>argThat(supplier -> supplier.get() == performerLocation));
        verify(callback).apply();
        verify(performer).applyReloadingState();
        verify(performer).resetReloadingState();
        verify(soundPlaySchedule).start();
        verify(reloadFinishSchedule).start();
    }

    @Test
    @DisplayName("performReload creates repeating schedules that adds loaded resources without playing sound sequence")
    void performReload_nullSoundSequence() {
        ResourceContainer resourceContainer = new ResourceContainer(30, 0, 1, 300);
        ReloadPerformer performer = mock(ReloadPerformer.class);
        Procedure callback = mock(Procedure.class);

        Schedule reloadFinishSchedule = mock(Schedule.class);
        doAnswer(MockUtils.answerRunScheduleTask()).when(reloadFinishSchedule).addTask(any(ScheduleTask.class));

        when(scheduler.createRepeatingSchedule(DURATION, DURATION)).thenReturn(reloadFinishSchedule);

        ManualInsertionReloadSystem reloadSystem = new ManualInsertionReloadSystem(scheduler, soundEmitter, PROPERTIES_NO_SOUND_SEQUENCE, resourceContainer);
        boolean activated = reloadSystem.performReload(performer, callback);

        assertThat(activated).isTrue();
        assertThat(resourceContainer.getLoadedAmount()).isOne();
        assertThat(resourceContainer.getReserveAmount()).isZero();

        verify(callback).apply();
        verify(performer).applyReloadingState();
        verify(performer).resetReloadingState();
        verify(reloadFinishSchedule).start();
        verifyNoInteractions(soundEmitter);
    }

    @Test
    @DisplayName("performReload does nothing when already performing")
    void performReload_alreadyPerforming() {
        ResourceContainer resourceContainer = new ResourceContainer(30, 0, 1, 300);
        ReloadPerformer performer = mock(ReloadPerformer.class);
        Schedule reloadFinishSchedule = mock(Schedule.class);

        Schedule soundPlaySchedule = mock(Schedule.class);
        doAnswer(MockUtils.answerRunScheduleTask()).when(soundPlaySchedule).addTask(any(ScheduleTask.class));

        when(scheduler.createRepeatingSchedule(0, DURATION)).thenReturn(soundPlaySchedule);
        when(scheduler.createRepeatingSchedule(DURATION, DURATION)).thenReturn(reloadFinishSchedule);

        ManualInsertionReloadSystem reloadSystem = new ManualInsertionReloadSystem(scheduler, soundEmitter, PROPERTIES_DEFAULT, resourceContainer);
        reloadSystem.performReload(performer, () -> {});
        boolean activated = reloadSystem.performReload(performer, () -> {});

        assertThat(activated).isFalse();

        verify(soundEmitter, times(1)).playSoundSequence(any(SoundSequence.class), Mockito.<Supplier<Location>>any());
        verify(scheduler, times(2)).createRepeatingSchedule(anyLong(), anyLong());
    }

    @Test
    @DisplayName("cancelReload does not cancel when not performing")
    void cancelReload_whenNotPerforming() {
        ResourceContainer resourceContainer = new ResourceContainer(30, 30, 90, 300);

        ManualInsertionReloadSystem reloadSystem = new ManualInsertionReloadSystem(scheduler, soundEmitter, PROPERTIES_DEFAULT, resourceContainer);
        boolean cancelled = reloadSystem.cancelReload();

        assertThat(cancelled).isFalse();
    }

    @Test
    @DisplayName("cancelReload resets performer state and cancels schedules")
    void cancelReload_whenPerforming() {
        ResourceContainer resourceContainer = new ResourceContainer(30, 30, 90, 300);
        Location performerLocation = new Location(null, 1, 1, 1);
        Schedule reloadFinishSchedule = mock(Schedule.class);
        SoundPlayback soundPlayback = mock(SoundPlayback.class);

        Schedule soundPlaySchedule = mock(Schedule.class);
        doAnswer(MockUtils.answerRunScheduleTask()).when(soundPlaySchedule).addTask(any(ScheduleTask.class));

        ReloadPerformer performer = mock(ReloadPerformer.class);
        when(performer.getLocation()).thenReturn(performerLocation);

        when(scheduler.createRepeatingSchedule(0, DURATION)).thenReturn(soundPlaySchedule);
        when(soundEmitter.playSoundSequence(eq(SOUND_SEQUENCE), Mockito.<Supplier<Location>>argThat(supplier -> supplier.get() == performerLocation))).thenReturn(soundPlayback);
        when(scheduler.createRepeatingSchedule(DURATION, DURATION)).thenReturn(reloadFinishSchedule);

        ManualInsertionReloadSystem reloadSystem = new ManualInsertionReloadSystem(scheduler, soundEmitter, PROPERTIES_DEFAULT, resourceContainer);
        reloadSystem.performReload(performer, () -> {});
        boolean cancelled = reloadSystem.cancelReload();

        assertThat(cancelled).isTrue();

        verify(performer).resetReloadingState();
        verify(reloadFinishSchedule).stop();
        verify(soundPlayback).cancel();
    }
}
