package nl.matsgemmeke.battlegrounds.item.reload.magazine;

import nl.matsgemmeke.battlegrounds.MockUtils;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadPerformer;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadProperties;
import nl.matsgemmeke.battlegrounds.item.reload.ResourceContainer;
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

import java.util.List;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MagazineReloadSystemTest {

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
        Schedule schedule = mock(Schedule.class);
        ReloadPerformer performer = mock(ReloadPerformer.class);

        when(scheduler.createSingleRunSchedule(DURATION)).thenReturn(schedule);

        MagazineReloadSystem reloadSystem = new MagazineReloadSystem(scheduler, soundEmitter, PROPERTIES_DEFAULT, resourceContainer);
        reloadSystem.performReload(performer, () -> {});
        boolean performing = reloadSystem.isPerforming();

        assertThat(performing).isTrue();
    }

    @Test
    @DisplayName("performReload creates single run schedules that play sounds and add full capacity when sufficient reserve resources are available")
    void performReload_fullCapacity() {
        ResourceContainer resourceContainer = new ResourceContainer(30, 0, 90, 300);
        Location performerLocation = new Location(null, 1, 1, 1);
        Procedure callback = mock(Procedure.class);

        ReloadPerformer performer = mock(ReloadPerformer.class);
        when(performer.getLocation()).thenReturn(performerLocation);

        Schedule schedule = mock(Schedule.class);
        doAnswer(MockUtils.answerRunScheduleTask()).when(schedule).addTask(any(ScheduleTask.class));

        when(scheduler.createSingleRunSchedule(DURATION)).thenReturn(schedule);

        MagazineReloadSystem reloadSystem = new MagazineReloadSystem(scheduler, soundEmitter, PROPERTIES_DEFAULT, resourceContainer);
        boolean performed = reloadSystem.performReload(performer, callback);

        assertThat(performed).isTrue();
        assertThat(resourceContainer.getLoadedAmount()).isEqualTo(30);
        assertThat(resourceContainer.getReserveAmount()).isEqualTo(60);

        verify(soundEmitter).playSoundSequence(eq(SOUND_SEQUENCE), Mockito.<Supplier<Location>>argThat(supplier -> supplier.get() == performerLocation));
        verify(callback).apply();
        verify(performer).applyReloadingState();
        verify(performer).resetReloadingState();
    }

    @Test
    @DisplayName("performReload creates single run schedules that play sounds and add partial capacity when not enough reserve resources are available")
    void performReload_partialCapacity() {
        ResourceContainer resourceContainer = new ResourceContainer(30, 0, 10, 300);
        Location performerLocation = new Location(null, 1, 1, 1);
        Procedure callback = mock(Procedure.class);

        ReloadPerformer performer = mock(ReloadPerformer.class);
        when(performer.getLocation()).thenReturn(performerLocation);

        Schedule schedule = mock(Schedule.class);
        doAnswer(MockUtils.answerRunScheduleTask()).when(schedule).addTask(any(ScheduleTask.class));

        when(scheduler.createSingleRunSchedule(DURATION)).thenReturn(schedule);

        MagazineReloadSystem reloadSystem = new MagazineReloadSystem(scheduler, soundEmitter, PROPERTIES_DEFAULT, resourceContainer);
        boolean performed = reloadSystem.performReload(performer, callback);

        assertThat(performed).isTrue();
        assertThat(resourceContainer.getLoadedAmount()).isEqualTo(10);
        assertThat(resourceContainer.getReserveAmount()).isZero();

        verify(soundEmitter).playSoundSequence(eq(SOUND_SEQUENCE), Mockito.<Supplier<Location>>argThat(supplier -> supplier.get() == performerLocation));
        verify(callback).apply();
        verify(performer).applyReloadingState();
        verify(performer).resetReloadingState();
    }

    @Test
    @DisplayName("performReload creates single run schedule that adds capacity without playing sound sequence")
    void performReload_nullSoundSequence() {
        ResourceContainer resourceContainer = new ResourceContainer(30, 0, 10, 300);
        Procedure callback = mock(Procedure.class);
        ReloadPerformer performer = mock(ReloadPerformer.class);

        Schedule schedule = mock(Schedule.class);
        doAnswer(MockUtils.answerRunScheduleTask()).when(schedule).addTask(any(ScheduleTask.class));

        when(scheduler.createSingleRunSchedule(DURATION)).thenReturn(schedule);

        MagazineReloadSystem reloadSystem = new MagazineReloadSystem(scheduler, soundEmitter, PROPERTIES_NO_SOUND_SEQUENCE, resourceContainer);
        boolean performed = reloadSystem.performReload(performer, callback);

        assertThat(performed).isTrue();
        assertThat(resourceContainer.getLoadedAmount()).isEqualTo(10);
        assertThat(resourceContainer.getReserveAmount()).isZero();

        verify(callback).apply();
        verify(performer).applyReloadingState();
        verify(performer).resetReloadingState();
        verifyNoInteractions(soundEmitter);
    }

    @Test
    @DisplayName("cancelReload does not cancel when not performing")
    void cancelReload_whenNotPerforming() {
        ResourceContainer resourceContainer = new ResourceContainer(30, 30, 90, 300);

        MagazineReloadSystem reloadSystem = new MagazineReloadSystem(scheduler, soundEmitter, PROPERTIES_DEFAULT, resourceContainer);
        boolean cancelled = reloadSystem.cancelReload();

        assertThat(cancelled).isFalse();
    }

    @Test
    @DisplayName("cancelReload resets performer state and cancels schedules")
    void cancelReload_whenPerforming() {
        ResourceContainer resourceContainer = new ResourceContainer(30, 30, 90, 300);
        Schedule schedule = mock(Schedule.class);
        Location performerLocation = new Location(null, 1, 1, 1);
        SoundPlayback soundPlayback = mock(SoundPlayback.class);

        ReloadPerformer performer = mock(ReloadPerformer.class);
        when(performer.getLocation()).thenReturn(performerLocation);

        when(soundEmitter.playSoundSequence(eq(SOUND_SEQUENCE), Mockito.<Supplier<Location>>argThat(supplier -> supplier.get() == performerLocation))).thenReturn(soundPlayback);
        when(scheduler.createSingleRunSchedule(DURATION)).thenReturn(schedule);

        MagazineReloadSystem reloadSystem = new MagazineReloadSystem(scheduler, soundEmitter, PROPERTIES_DEFAULT, resourceContainer);
        reloadSystem.performReload(performer, () -> {});
        boolean cancelled = reloadSystem.cancelReload();

        assertThat(cancelled).isTrue();

        verify(performer).resetReloadingState();
        verify(soundPlayback).cancel();
        verify(schedule).stop();
    }
}
