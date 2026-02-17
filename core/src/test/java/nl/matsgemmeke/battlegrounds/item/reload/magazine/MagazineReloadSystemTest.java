package nl.matsgemmeke.battlegrounds.item.reload.magazine;

import nl.matsgemmeke.battlegrounds.MockUtils;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadPerformer;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadProperties;
import nl.matsgemmeke.battlegrounds.item.reload.ResourceContainer;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.ScheduleTask;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import nl.matsgemmeke.battlegrounds.util.Procedure;
import org.bukkit.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MagazineReloadSystemTest {

    private static final long GAME_SOUND_DELAY = 20L;
    private static final long DURATION = 50L;

    private ResourceContainer resourceContainer;

    @Mock
    private AudioEmitter audioEmitter;
    @Mock
    private GameSound gameSound;
    @Mock
    private Scheduler scheduler;

    private MagazineReloadSystem reloadSystem;

    @BeforeEach
    void setUp() {
        ReloadProperties properties = new ReloadProperties(List.of(gameSound), DURATION);
        resourceContainer = new ResourceContainer(30, 30, 90, 300);

        reloadSystem = new MagazineReloadSystem(audioEmitter, scheduler, properties, resourceContainer);
    }

    @Test
    @DisplayName("isPerforming returns false when reload is not activated")
    void isPerforming_notReloading() {
        boolean performing = reloadSystem.isPerforming();

        assertThat(performing).isFalse();
    }

    @Test
    @DisplayName("isPerforming returns true when reload is activated")
    void isPerforming_reloading() {
        Schedule soundPlaySchedule = mock(Schedule.class);
        Schedule reloadFinishSchedule = mock(Schedule.class);
        ReloadPerformer performer = mock(ReloadPerformer.class);

        when(gameSound.getDelay()).thenReturn(GAME_SOUND_DELAY);
        when(scheduler.createSingleRunSchedule(GAME_SOUND_DELAY)).thenReturn(soundPlaySchedule);
        when(scheduler.createSingleRunSchedule(DURATION)).thenReturn(reloadFinishSchedule);

        reloadSystem.performReload(performer, () -> {});
        boolean performing = reloadSystem.isPerforming();

        assertThat(performing).isTrue();
    }

    @Test
    @DisplayName("performReload creates single run schedules that play sounds and add full capacity when enough reserve resources are available")
    void performReload_fullCapacity() {
        Location performerLocation = new Location(null, 1, 1, 1);
        Procedure callback = mock(Procedure.class);

        ReloadPerformer performer = mock(ReloadPerformer.class);
        when(performer.getLocation()).thenReturn(performerLocation);

        Schedule soundPlaySchedule = mock(Schedule.class);
        doAnswer(MockUtils.answerRunScheduleTask()).when(soundPlaySchedule).addTask(any(ScheduleTask.class));

        Schedule reloadFinishSchedule = mock(Schedule.class);
        doAnswer(MockUtils.answerRunScheduleTask()).when(reloadFinishSchedule).addTask(any(ScheduleTask.class));

        resourceContainer.setLoadedAmount(0);

        when(gameSound.getDelay()).thenReturn(GAME_SOUND_DELAY);
        when(scheduler.createSingleRunSchedule(GAME_SOUND_DELAY)).thenReturn(soundPlaySchedule);
        when(scheduler.createSingleRunSchedule(DURATION)).thenReturn(reloadFinishSchedule);

        boolean performed = reloadSystem.performReload(performer, callback);

        assertThat(performed).isTrue();
        assertThat(resourceContainer.getLoadedAmount()).isEqualTo(30);
        assertThat(resourceContainer.getReserveAmount()).isEqualTo(60);

        verify(audioEmitter).playSound(gameSound, performerLocation);
        verify(callback).apply();
        verify(performer).applyReloadingState();
        verify(performer).resetReloadingState();
    }

    @Test
    @DisplayName("performReload creates single run schedules that play sounds and add partial capacity when not enough reserve resources are available")
    void performReload_partialCapacity() {
        Location performerLocation = new Location(null, 1, 1, 1);
        Procedure callback = mock(Procedure.class);

        ReloadPerformer performer = mock(ReloadPerformer.class);
        when(performer.getLocation()).thenReturn(performerLocation);

        Schedule soundPlaySchedule = mock(Schedule.class);
        doAnswer(MockUtils.answerRunScheduleTask()).when(soundPlaySchedule).addTask(any(ScheduleTask.class));

        Schedule reloadFinishSchedule = mock(Schedule.class);
        doAnswer(MockUtils.answerRunScheduleTask()).when(reloadFinishSchedule).addTask(any(ScheduleTask.class));

        resourceContainer.setLoadedAmount(0);
        resourceContainer.setReserveAmount(10);

        when(gameSound.getDelay()).thenReturn(GAME_SOUND_DELAY);
        when(scheduler.createSingleRunSchedule(GAME_SOUND_DELAY)).thenReturn(soundPlaySchedule);
        when(scheduler.createSingleRunSchedule(DURATION)).thenReturn(reloadFinishSchedule);

        boolean performed = reloadSystem.performReload(performer, callback);

        assertThat(performed).isTrue();
        assertThat(resourceContainer.getLoadedAmount()).isEqualTo(10);
        assertThat(resourceContainer.getReserveAmount()).isZero();

        verify(audioEmitter).playSound(gameSound, performerLocation);
        verify(callback).apply();
        verify(performer).applyReloadingState();
        verify(performer).resetReloadingState();
    }

    @Test
    @DisplayName("cancelReload does not cancel when not performing")
    void cancelReload_whenNotPerforming() {
        boolean cancelled = reloadSystem.cancelReload();

        assertThat(cancelled).isFalse();
    }

    @Test
    @DisplayName("cancelReload resets performer state and cancels schedules")
    void cancelReload_whenPerforming() {
        Schedule soundPlaySchedule = mock(Schedule.class);
        Schedule reloadFinishSchedule = mock(Schedule.class);
        ReloadPerformer performer = mock(ReloadPerformer.class);

        when(gameSound.getDelay()).thenReturn(GAME_SOUND_DELAY);
        when(scheduler.createSingleRunSchedule(GAME_SOUND_DELAY)).thenReturn(soundPlaySchedule);
        when(scheduler.createSingleRunSchedule(DURATION)).thenReturn(reloadFinishSchedule);

        reloadSystem.performReload(performer, () -> {});
        boolean cancelled = reloadSystem.cancelReload();

        assertThat(cancelled).isTrue();

        verify(performer).resetReloadingState();
        verify(reloadFinishSchedule).stop();
    }
}
