package nl.matsgemmeke.battlegrounds.item.effect.simulation;

import nl.matsgemmeke.battlegrounds.MockUtils;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.info.gun.GunFireSimulationInfo;
import nl.matsgemmeke.battlegrounds.game.component.info.gun.GunInfoProvider;
import nl.matsgemmeke.battlegrounds.game.damage.DamageSource;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.source.ItemEffectSource;
import nl.matsgemmeke.battlegrounds.item.effect.source.Removable;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.ScheduleTask;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import org.bukkit.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static nl.matsgemmeke.battlegrounds.MockUtils.RUN_SCHEDULE_TASK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GunFireSimulationEffectPerformanceTest {

    private static final Location INITIATION_LOCATION = new Location(null, 0, 0, 0);
    private static final Location EFFECT_SOURCE_LOCATION = new Location(null, 1, 1, 1);
    private static final long BURST_INTERVAL = 1L;
    private static final long MAX_BURST_DURATION = 10L;
    private static final long MIN_BURST_DURATION = 5L;
    private static final long MAX_DELAY_DURATION = 10L;
    private static final long MIN_DELAY_DURATION = 5L;
    private static final long MAX_TOTAL_DURATION = 30L;
    private static final long MIN_TOTAL_DURATION = 20L;
    private static final List<GameSound> GENERIC_SHOTS_SOUNDS = Collections.emptyList();
    private static final GunFireSimulationProperties PROPERTIES = new GunFireSimulationProperties(GENERIC_SHOTS_SOUNDS, BURST_INTERVAL, MIN_BURST_DURATION, MAX_BURST_DURATION, MIN_DELAY_DURATION, MAX_DELAY_DURATION, MIN_TOTAL_DURATION, MAX_TOTAL_DURATION);

    private static final UUID DAMAGE_SOURCE_ID = UUID.randomUUID();

    @Mock
    private AudioEmitter audioEmitter;
    @Mock
    private DamageSource damageSource;
    @Mock
    private GunInfoProvider gunInfoProvider;
    @Mock(extraInterfaces = Removable.class)
    private ItemEffectSource effectSource;
    @Mock
    private Scheduler scheduler;

    private GunFireSimulationEffectPerformance performance;

    @BeforeEach
    void setUp() {
        performance = new GunFireSimulationEffectPerformance(audioEmitter, gunInfoProvider, scheduler, PROPERTIES);
    }

    @Test
    void changeEffectSourceCreatesNewContextInstanceWithGivenEffectSource() {
        Schedule schedule = mock(Schedule.class);
        ItemEffectSource oldEffectSource = mock(ItemEffectSource.class);
        ItemEffectContext context = new ItemEffectContext(damageSource, oldEffectSource, INITIATION_LOCATION);
        Location newSourceLocation = new Location(null, 1, 1, 1);

        when(damageSource.getUniqueId()).thenReturn(DAMAGE_SOURCE_ID);
        when(effectSource.exists()).thenReturn(true);
        when(effectSource.getLocation()).thenReturn(newSourceLocation);
        when(gunInfoProvider.getGunFireSimulationInfo(DAMAGE_SOURCE_ID)).thenReturn(Optional.empty());
        when(scheduler.createRepeatingSchedule(0L, 1L)).thenReturn(schedule);

        performance.setContext(context);
        performance.start();
        performance.changeEffectSource(effectSource);

        ArgumentCaptor<ScheduleTask> taskCaptor = ArgumentCaptor.forClass(ScheduleTask.class);
        verify(schedule).addTask(taskCaptor.capture());

        ScheduleTask task = taskCaptor.getValue();
        task.run();

        verify(audioEmitter).playSounds(GENERIC_SHOTS_SOUNDS, newSourceLocation);
    }

    @Test
    void isPerformingReturnsFalseWhenNotPerforming() {
        boolean performing = performance.isPerforming();

        assertThat(performing).isFalse();
    }

    @Test
    void isPerformingReturnsTrueWhenPerforming() {
        ItemEffectContext context = new ItemEffectContext(damageSource, effectSource, INITIATION_LOCATION);

        Schedule repeatingSchedule = mock(Schedule.class);
        when(repeatingSchedule.isRunning()).thenReturn(true);

        when(scheduler.createRepeatingSchedule(0L, 1L)).thenReturn(repeatingSchedule);

        performance.perform(context);
        boolean performing = performance.isPerforming();

        assertThat(performing).isTrue();
    }

    @Test
    void performSimulatesGenericGunFireWhenGunInfoProviderHasNoInformationForEntity() {
        Schedule repeatingSchedule = mock(Schedule.class);
        ItemEffectContext context = new ItemEffectContext(damageSource, effectSource, INITIATION_LOCATION);

        when(damageSource.getUniqueId()).thenReturn(DAMAGE_SOURCE_ID);
        when(effectSource.exists()).thenReturn(true);
        when(effectSource.getLocation()).thenReturn(EFFECT_SOURCE_LOCATION);
        when(gunInfoProvider.getGunFireSimulationInfo(DAMAGE_SOURCE_ID)).thenReturn(Optional.empty());
        when(scheduler.createRepeatingSchedule(0L, 1L)).thenReturn(repeatingSchedule);
        doAnswer(RUN_SCHEDULE_TASK).when(repeatingSchedule).addTask(any(ScheduleTask.class));

        performance.setContext(context);
        performance.start();

        verify(audioEmitter).playSounds(GENERIC_SHOTS_SOUNDS, EFFECT_SOURCE_LOCATION);
    }

    @Test
    void performStopsSimulatesGunFireOnceEffectSourceNoLongerExists() {
        Schedule repeatingSchedule = mock(Schedule.class);
        List<GameSound> shotSounds = Collections.emptyList();
        int rateOfFire = 120;
        GunFireSimulationInfo gunFireSimulationInfo = new GunFireSimulationInfo(shotSounds, rateOfFire);

        ItemEffectContext context = new ItemEffectContext(damageSource, effectSource, INITIATION_LOCATION);

        when(damageSource.getUniqueId()).thenReturn(DAMAGE_SOURCE_ID);
        when(effectSource.exists()).thenReturn(false);
        when(gunInfoProvider.getGunFireSimulationInfo(DAMAGE_SOURCE_ID)).thenReturn(Optional.of(gunFireSimulationInfo));
        when(scheduler.createRepeatingSchedule(0L, 1L)).thenReturn(repeatingSchedule);
        doAnswer(RUN_SCHEDULE_TASK).when(repeatingSchedule).addTask(any(ScheduleTask.class));

        performance.setContext(context);
        performance.start();

        verifyNoInteractions(audioEmitter);
        verify(repeatingSchedule).stop();
    }

    @Test
    void performSimulatesGunFireOnceAndRemovesEffectSourceWhenFinished() {
        Schedule repeatingSchedule = mock(Schedule.class);
        ItemEffectContext context = new ItemEffectContext(damageSource, effectSource, INITIATION_LOCATION);

        List<GameSound> shotSounds = Collections.emptyList();
        int rateOfFire = 1200;
        GunFireSimulationInfo gunFireSimulationInfo = new GunFireSimulationInfo(shotSounds, rateOfFire);

        when(damageSource.getUniqueId()).thenReturn(DAMAGE_SOURCE_ID);
        when(effectSource.exists()).thenReturn(true);
        when(effectSource.getLocation()).thenReturn(EFFECT_SOURCE_LOCATION);
        when(gunInfoProvider.getGunFireSimulationInfo(DAMAGE_SOURCE_ID)).thenReturn(Optional.of(gunFireSimulationInfo));
        when(scheduler.createRepeatingSchedule(0L, 1L)).thenReturn(repeatingSchedule);
        doAnswer(MockUtils.answerScheduleTaskRun(MAX_TOTAL_DURATION)).when(repeatingSchedule).addTask(any(ScheduleTask.class));

        performance.setContext(context);
        performance.start();

        // The implementation uses random variables, so check the max and min possible amount of executions
        verify(audioEmitter, atLeast(10)).playSounds(shotSounds, EFFECT_SOURCE_LOCATION);
        verify(audioEmitter, atMost(20)).playSounds(shotSounds, EFFECT_SOURCE_LOCATION);
        verify(repeatingSchedule, atLeast(1)).stop();
        verify(repeatingSchedule, atMost(10)).stop();
        verify((Removable) effectSource, atLeast(1)).remove();
        verify((Removable) effectSource, atMost(10)).remove();
    }
}
